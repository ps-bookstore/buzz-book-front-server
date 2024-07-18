package store.buzzbook.front.controller.admin.point;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.buzzbook.front.common.util.CookieUtils.*;

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
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.client.point.PointClient;
import store.buzzbook.front.common.config.SecurityConfig;
import store.buzzbook.front.common.config.WebConfig;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.point.CreatePointPolicyRequest;
import store.buzzbook.front.dto.point.DeletePointPolicyRequest;
import store.buzzbook.front.dto.point.PointPolicyResponse;
import store.buzzbook.front.dto.point.UpdatePointPolicyRequest;
import store.buzzbook.front.service.jwt.JwtService;

@ActiveProfiles("test")
@WebMvcTest(AdminPointController.class)
@Import({WebConfig.class, SecurityConfig.class})
class AdminPointControllerTest {

	private MockMvc mockMvc;

	@MockBean
	private PointClient pointClient;

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
	@DisplayName("get point policies")
	void getPointPolicies() throws Exception {
		// given
		when(pointClient.getPointPolicies()).thenReturn(Collections.emptyList());

		// when & then
		mockMvc.perform(get("/admin/points"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("pointPolicies"))
			.andExpect(model().attributeExists("page"))
			.andExpect(model().attribute("page", "point-manage"));
	}

	@Test
	@DisplayName("create point policy get")
	void createPointPolicy_Get() throws Exception {
		// when & then
		mockMvc.perform(get("/admin/points/policies"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("page"))
			.andExpect(model().attribute("page", "create-point-policy"));
	}

	@Test
	@DisplayName("create point policy post")
	void createPointPolicy_Post() throws Exception {
		// given
		CreatePointPolicyRequest request = new CreatePointPolicyRequest("test", 1000, 0.5);

		// when & then
		mockMvc.perform(post("/admin/points/policies")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", request.name())
				.param("point", String.valueOf(request.point()))
				.param("rate", String.valueOf(request.rate())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/points"));
	}

	@Test
	@DisplayName("update point policy")
	void updatePointPolicy() throws Exception {
		// given
		PointPolicyResponse request = new PointPolicyResponse(1L, "test", 1000, 0.5, false);

		// when & then
		mockMvc.perform(post("/admin/points/policies/edit")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(request.id()))
				.param("name", request.name())
				.param("point", String.valueOf(request.point()))
				.param("rate", String.valueOf(request.rate()))
				.param("deleted", String.valueOf(request.deleted())))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("pointPolicy"))
			.andExpect(model().attributeExists("page"))
			.andExpect(model().attribute("page", "update-point-policy"));
	}

	@Test
	@DisplayName("update point policy process")
	void updatePointPolicy_Process() throws Exception {
		// given
		UpdatePointPolicyRequest request = new UpdatePointPolicyRequest(1L, 1000, 0);

		// when & then
		mockMvc.perform(post("/admin/points/policies/edit/process")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(request.id()))
				.param("point", String.valueOf(request.point()))
				.param("rate", String.valueOf(request.rate())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/points"));
	}

	@Test
	@DisplayName("delete point policy")
	void deletePointPolicy_Delete() throws Exception {
		// given
		DeletePointPolicyRequest request = new DeletePointPolicyRequest(1L);

		// when & then
		mockMvc.perform(post("/admin/points/policies/delete")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(request.id())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/points"));
	}

}
