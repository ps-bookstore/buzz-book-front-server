package store.buzzbook.front.controller.user;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.client.jwt.JwtClient;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.user.Grade;
import store.buzzbook.front.dto.user.GradeName;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.user.UserAuthService;
import store.buzzbook.front.service.user.UserService;

@ActiveProfiles("test")
@WebMvcTest(controllers = {RegisterController.class, LogoutController.class})
class SignControllerTest {
	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CookieUtils cookieUtils;

	@MockBean
	private JwtClient jwtClient;

	@MockBean
	private CartInterceptor cartInterceptor;
	@MockBean
	private UserService userService;
	@MockBean
	private UserAuthService userAuthService;

	private String password;
	private Grade grade;
	private UserInfo userInfo;
	private RegisterUserRequest registerUserRequest;

	@BeforeEach
	void setUp() {
		password = "testPassword";

		grade = Grade.builder()
			.id(1)
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		userInfo = UserInfo.builder()
			.loginId("testid00000000")
			.name("john doe")
			.email("email123@nhn.com")
			.contactNumber("01000001111")
			.birthday(LocalDate.now().minusMonths(1))
			.id(1L)
			.point(132)
			.grade(grade)
			.isAdmin(false).build();

		registerUserRequest = new RegisterUserRequest(
			userInfo.getLoginId(),
			password,
			password,
			userInfo.getName(),
			userInfo.getContactNumber(),
			userInfo.getEmail(),
			true,
			userInfo.getBirthday()
		);

		Cookie accessCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, "accessTokenValue");
		Cookie refreshCookie = new Cookie(CookieUtils.COOKIE_JWT_REFRESH_KEY, "refreshTokenValue");

		when(cookieUtils.getCookie(any(HttpServletRequest.class), eq(CookieUtils.COOKIE_JWT_ACCESS_KEY)))
			.thenReturn(Optional.of(accessCookie));
		when(cookieUtils.getCookie(any(HttpServletRequest.class), eq(CookieUtils.COOKIE_JWT_REFRESH_KEY)))
			.thenReturn(Optional.of(refreshCookie));

	}


	@WithMockUser
	@Test
	void testRegisterForm() throws Exception {
		mockMvc.perform(get("/signup"))
			.andExpect(status().isOk())
			.andExpect(view().name("pages/register/signup"));
	}

	@WithMockUser
	@Test
	void testRegisterSubmitSuccess() throws Exception {
		doNothing().when(userService).registerUser(any(RegisterUserRequest.class));


		mockMvc.perform(post("/signup").with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId", registerUserRequest.loginId())
				.param("name", registerUserRequest.name())
				.param("contactNumber", registerUserRequest.contactNumber())
				.param("email", registerUserRequest.email())
				.param("birthday", registerUserRequest.birthday().toString())
				.param("password", registerUserRequest.password())
				.param("confirmedPassword", registerUserRequest.confirmedPassword()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/welcome?id=" + registerUserRequest.loginId()));

		verify(userService, times(1)).registerUser(any(RegisterUserRequest.class));
	}

	@WithMockUser
	@Test
	void testWelcome() throws Exception {
		mockMvc.perform(get("/welcome")
				.param("id", userInfo.getLoginId()))
			.andExpect(status().isOk())
			.andExpect(view().name("pages/register/signup-success"))
			.andExpect(model().attribute("id", userInfo.getLoginId()));
	}

}
