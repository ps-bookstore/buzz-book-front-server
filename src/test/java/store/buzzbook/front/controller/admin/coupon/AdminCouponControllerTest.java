package store.buzzbook.front.controller.admin.coupon;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.buzzbook.front.common.util.CookieUtils.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.client.coupon.CouponPolicyClient;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.common.config.SecurityConfig;
import store.buzzbook.front.common.config.WebConfig;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.coupon.CouponPolicyConditionRequest;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.coupon.CreateCouponPolicyRequest;
import store.buzzbook.front.service.jwt.JwtService;

@ActiveProfiles("test")
@WebMvcTest(AdminCouponController.class)
@Import({WebConfig.class, SecurityConfig.class})
class AdminCouponControllerTest {

	private MockMvc mockMvc;

	@MockBean
	private CouponPolicyClient couponPolicyClient;

	@MockBean
	private ProductClient productClient;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private CookieUtils cookieUtils;

	@MockBean
	private CartInterceptor cartInterceptor;

	@MockBean
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();

		Cookie mockCookie = new Cookie(COOKIE_JWT_ACCESS_KEY, "valid-token");
		when(cookieUtils.getCookie(any(HttpServletRequest.class), anyString())).thenReturn(
			Optional.of(mockCookie));
		when(jwtService.getInfoMapFromJwt(any(HttpServletRequest.class), any(HttpServletResponse.class)))
			.thenReturn(Map.of(
				JwtService.USER_ID, 1,
				JwtService.LOGIN_ID, "test",
				JwtService.ROLE, "ROLE_USER"
			));
	}

	@Test
	@DisplayName("coupon manage")
	void couponManage() throws Exception {
		// given
		Page<CouponPolicyResponse> couponPolicies = new PageImpl<>(Collections.emptyList());
		when(couponPolicyClient.getCouponPoliciesByPaging(any(CouponPolicyConditionRequest.class)))
			.thenReturn(couponPolicies);
		when(couponPolicyClient.getCouponTypes()).thenReturn(Collections.emptyList());

		// when & then
		mockMvc.perform(get("/admin/coupons")
				.param("discountTypeName", "ALL")
				.param("isDeleted", "ALL")
				.param("couponTypeName", "ALL"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("couponTypes"))
			.andExpect(model().attributeExists("couponPolicies"))
			.andExpect(model().attributeExists("page"))
			.andExpect(model().attribute("page", "couponManage"));
	}

	@Test
	@DisplayName("create coupon policy")
	void createCouponPolicy() throws Exception {
		// given
		when(couponPolicyClient.getCouponTypes()).thenReturn(Collections.emptyList());

		// when & then
		mockMvc.perform(get("/admin/coupons/policies"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("couponTypes"))
			.andExpect(model().attributeExists("page"))
			.andExpect(model().attribute("page", "couponPolicy"));
	}

	@Test
	@DisplayName("search products")
	void searchProducts() throws Exception {
		// given
		when(productClient.searchProducts(anyString())).thenReturn(Collections.emptyList());

		// when & then
		mockMvc.perform(get("/admin/coupons/policies/product-search")
				.param("query", "test"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	@DisplayName("search category")
	void searchCategory() throws Exception {
		// given
		when(productClient.getAllCategories()).thenReturn(Collections.emptyList());

		// when & then
		mockMvc.perform(get("/admin/coupons/policies/category-search"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	@DisplayName("create coupon policy post")
	void createCouponPolicyForCategory() throws Exception {
		// given
		CreateCouponPolicyRequest request = new CreateCouponPolicyRequest(
			"test",
			"amount",
			0.0,
			1000,
			10000,
			20000,
			14,
			LocalDate.now().toString(),
			LocalDate.now().plusDays(1).toString(),
			"global",
			0);

		// when & then
		mockMvc.perform(post("/admin/coupons/policies")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", request.name())
				.param("discountType", request.discountType())
				.param("discountRate", String.valueOf(request.discountRate()))
				.param("discountAmount", String.valueOf(request.discountAmount()))
				.param("standardPrice", String.valueOf(request.standardPrice()))
				.param("maxDiscountAmount", String.valueOf(request.maxDiscountAmount()))
				.param("period", String.valueOf(request.period()))
				.param("startDate", request.startDate())
				.param("endDate", request.endDate())
				.param("couponType", request.couponType())
				.param("targetId", String.valueOf(request.targetId())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/coupons"));
	}

	@Test
	@DisplayName("create coupon policy post with invalid data")
	void createCouponPolicyWithInvalidData() throws Exception {
		// given
		CreateCouponPolicyRequest invalidRequest = new CreateCouponPolicyRequest(
			"",
			"amount",
			1.5,
			-1000,
			10000,
			20000,
			14,
			LocalDate.now().toString(),
			LocalDate.now().plusDays(1).toString(),
			"global",
			0
		);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders.post("/admin/coupons/policies")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", invalidRequest.name())
				.param("discountType", invalidRequest.discountType())
				.param("discountRate", String.valueOf(invalidRequest.discountRate()))
				.param("discountAmount", String.valueOf(invalidRequest.discountAmount()))
				.param("standardPrice", String.valueOf(invalidRequest.standardPrice()))
				.param("maxDiscountAmount", String.valueOf(invalidRequest.maxDiscountAmount()))
				.param("period", String.valueOf(invalidRequest.period()))
				.param("startDate", invalidRequest.startDate())
				.param("endDate", invalidRequest.endDate())
				.param("couponType", invalidRequest.couponType())
				.param("targetId", String.valueOf(invalidRequest.targetId())))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("delete coupon policy")
	void deleteCouponPolicyForCategory() throws Exception {
		// when & then
		mockMvc.perform(get("/admin/coupons/policies/delete")
				.param("couponPolicyId", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/coupons"));
	}
}

