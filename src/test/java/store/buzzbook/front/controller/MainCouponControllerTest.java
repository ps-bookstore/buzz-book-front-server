package store.buzzbook.front.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.buzzbook.front.common.util.CookieUtils.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.client.user.UserClient;
import store.buzzbook.front.common.config.SecurityConfig;
import store.buzzbook.front.common.config.WebConfig;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.controller.coupon.MainCouponController;
import store.buzzbook.front.dto.coupon.DownloadCouponRequest;
import store.buzzbook.front.service.jwt.JwtService;

@ActiveProfiles("test")
@WebMvcTest(MainCouponController.class)
@Import({WebConfig.class, SecurityConfig.class})
class MainCouponControllerTest {

	private MockMvc mockMvc;

	@MockBean
	private UserClient userClient;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private CookieUtils cookieUtils;

	@MockBean
	private CartInterceptor cartInterceptor;

	@MockBean
	private HttpServletRequest httpServletRequest;

	@MockBean
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

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
	@DisplayName("download specific coupon - success")
	void downloadCouponSuccess() throws Exception {
		// given
		Map<String, Integer> request = Map.of("couponPolicyId", 1);
		doNothing().when(userClient).downloadCoupon(any(DownloadCouponRequest.class));

		// when & then
		mockMvc.perform(post("/coupons/download-coupon")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(httpServletRequest -> {
					httpServletRequest.setAttribute(JwtService.USER_ID, 1L);
					return httpServletRequest;
				})
				.header("Authorization", "Bearer valid-token"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("download specific coupon - conflict")
	void downloadCouponConflict() throws Exception {
		// given
		Map<String, Integer> request = Map.of("couponPolicyId", 1);

		doThrow(new RuntimeException("Coupon already downloaded"))
			.when(userClient).downloadCoupon(any(DownloadCouponRequest.class));

		// when & then
		mockMvc.perform(post("/coupons/download-coupon")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("Authorization", "Bearer valid-token"))
			.andExpect(status().isConflict());
	}
}
