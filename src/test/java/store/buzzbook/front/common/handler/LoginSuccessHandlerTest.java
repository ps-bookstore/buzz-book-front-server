package store.buzzbook.front.common.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.common.exception.user.DormantUserException;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.jwt.AuthRequest;
import store.buzzbook.front.dto.user.Grade;
import store.buzzbook.front.dto.user.GradeName;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.UserService;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class LoginSuccessHandlerTest {

	@Mock
	private UserService userService;

	@Mock
	private JwtService jwtService;

	@Mock
	private CookieUtils cookieUtils;

	@InjectMocks
	private LoginSuccessHandler loginSuccessHandler;


	private String dormantToken;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Authentication authentication;
	private MockMvc mockMvc;
	private UserInfo userInfo;
	private Grade grade;

	@BeforeEach
	void setUp() {
		dormantToken = "dormantToken";
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		authentication = mock(Authentication.class);

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
			.contactNumber("010-0000-1111")
			.birthday(LocalDate.now().minusMonths(1))
			.id(1L)
			.point(132)
			.grade(grade)
			.isAdmin(false).build();

		when(authentication.getName()).thenReturn(userInfo.getLoginId());
	}

	@Test
	void testOnAuthenticationSuccess() throws Exception {
		when(userService.successLogin(userInfo.getLoginId())).thenReturn(userInfo);
		when(jwtService.accessToken(any(AuthRequest.class))).thenReturn("mockAccessToken");
		when(jwtService.refreshToken(any(AuthRequest.class))).thenReturn("mockRefreshToken");

		loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

		verify(response, times(2)).addCookie(any(Cookie.class));
		verify(response).sendRedirect(request.getContextPath()+"/home");
	}

	@Test
	void testOnAuthenticationSuccessAdmin() throws Exception {
		userInfo = UserInfo.builder()
			.loginId("testid00000000")
			.name("john doe")
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(LocalDate.now().minusMonths(1))
			.id(1L)
			.point(132)
			.grade(grade)
			.isAdmin(true).build();

		when(userService.successLogin(userInfo.getLoginId())).thenReturn(userInfo);
		when(jwtService.accessToken(any(AuthRequest.class))).thenReturn("mockAccessToken");
		when(jwtService.refreshToken(any(AuthRequest.class))).thenReturn("mockRefreshToken");

		loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

		verify(response, times(2)).addCookie(any(Cookie.class));
		verify(response).sendRedirect(request.getContextPath()+"/home");
	}

	@Test
	void testOnAuthenticationInvalidToken() throws Exception {
		when(userService.successLogin(userInfo.getLoginId())).thenReturn(userInfo);
		when(jwtService.accessToken(any(AuthRequest.class))).thenReturn(null);
		when(jwtService.refreshToken(any(AuthRequest.class))).thenReturn(null);

		loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

		verify(response).sendRedirect(request.getContextPath()+"/login");
	}

	@Test
	void testOnAuthenticationInvalidToken1() throws Exception {
		when(userService.successLogin(userInfo.getLoginId())).thenReturn(userInfo);
		when(jwtService.accessToken(any(AuthRequest.class))).thenReturn("mockAccessToken");
		when(jwtService.refreshToken(any(AuthRequest.class))).thenReturn(null);

		loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

		verify(response).sendRedirect(request.getContextPath()+"/login");
	}

	@Test
	void testOnAuthenticationInvalidToken2() throws Exception {
		when(userService.successLogin(userInfo.getLoginId())).thenReturn(userInfo);
		when(jwtService.accessToken(any(AuthRequest.class))).thenReturn(null);
		when(jwtService.refreshToken(any(AuthRequest.class))).thenReturn("mockRefreshToken");

		loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

		verify(response).sendRedirect(request.getContextPath()+"/login");
	}


	@Test
	void testOnAuthenticationSuccessWithDormantUserException() throws Exception {
		when(userService.successLogin(userInfo.getLoginId())).thenThrow(new DormantUserException(dormantToken));

		loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

		verify(response).sendRedirect(request.getContextPath()+"/activate?token="+dormantToken);
	}

	@Test
	void testOnAuthenticationSuccessWithException() throws Exception {
		when(userService.successLogin(userInfo.getLoginId())).thenThrow(new RuntimeException("error"));

		loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

		verify(response).sendRedirect(request.getContextPath()+"/login");
	}
}
