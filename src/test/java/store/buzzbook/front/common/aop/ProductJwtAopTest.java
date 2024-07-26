package store.buzzbook.front.common.aop;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static store.buzzbook.front.common.util.CookieUtils.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.common.config.SecurityConfig;
import store.buzzbook.front.common.config.WebConfig;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.service.jwt.JwtService;

@ActiveProfiles("test")
@Import({WebConfig.class, SecurityConfig.class})
class ProductJwtAopTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private CookieUtils cookieUtils;

	@InjectMocks
	private ProductJwtAop productJwtAop;

	@MockBean
	private CartInterceptor cartInterceptor;

	@MockBean
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	void testAuthenticate_AdminUser() {
		// Arrange
		Cookie authCookie = new Cookie(COOKIE_JWT_ACCESS_KEY, "authToken");
		Cookie refreshCookie = new Cookie(COOKIE_JWT_REFRESH_KEY, "refreshToken");

		when(cookieUtils.getCookie(any(HttpServletRequest.class), eq(COOKIE_JWT_ACCESS_KEY)))
			.thenReturn(Optional.of(authCookie));
		when(cookieUtils.getCookie(any(HttpServletRequest.class), eq(COOKIE_JWT_REFRESH_KEY)))
			.thenReturn(Optional.of(refreshCookie));
		when(jwtService.getInfoMapFromJwt(any(HttpServletRequest.class), any(HttpServletResponse.class)))
			.thenReturn(Map.of(
				JwtService.USER_ID, 1L,
				JwtService.LOGIN_ID, "admin",
				JwtService.ROLE, "ADMIN"
			));

		// Act & Assert
		assertDoesNotThrow(() -> productJwtAop.authenticate(), "ADMIN 사용자로 인증 실패");
	}


	@Test
	void testAuthenticate_User() throws Throwable {
	
		setupCookiesAndJwt("user", "USER");
		
		assertThrows(AuthorizeFailException.class, () -> productJwtAop.authenticate(), "접근 권한이 없습니다.");
	}

	@Test
	void testAuthenticate_NoCookies() throws Throwable {
		
		when(cookieUtils.getCookie(any(HttpServletRequest.class), anyString())).thenReturn(Optional.empty());
		
		assertThrows(AuthorizeFailException.class, () -> productJwtAop.authenticate(), "로그인을 해주세요");
	}

	@Test
	void testAuthenticate_InvalidRole() throws Throwable {
		
		Cookie authCookie = new Cookie(COOKIE_JWT_ACCESS_KEY, "authToken");
		when(cookieUtils.getCookie(any(HttpServletRequest.class), eq(COOKIE_JWT_ACCESS_KEY)))
			.thenReturn(Optional.of(authCookie));
		when(jwtService.getInfoMapFromJwt(any(HttpServletRequest.class), any(HttpServletResponse.class)))
			.thenReturn(Map.of(
				JwtService.USER_ID, 1L,
				JwtService.LOGIN_ID, "user",
				JwtService.ROLE, "USER"
			));

		assertThrows(AuthorizeFailException.class, () -> productJwtAop.authenticate(), "접근 권한이 없습니다.");
	}

	private void setupCookiesAndJwt(String loginId, String role) {
		Cookie authCookie = new Cookie(COOKIE_JWT_ACCESS_KEY, "authToken");
		Cookie refreshCookie = new Cookie(CookieUtils.COOKIE_JWT_REFRESH_KEY, "refreshToken");

		//모킹 설정
		when(cookieUtils.getCookie(any(HttpServletRequest.class), eq(COOKIE_JWT_ACCESS_KEY)))
			.thenReturn(Optional.of(authCookie));
		when(cookieUtils.getCookie(any(HttpServletRequest.class), eq(CookieUtils.COOKIE_JWT_REFRESH_KEY)))
			.thenReturn(Optional.of(refreshCookie));
		when(jwtService.getInfoMapFromJwt(any(HttpServletRequest.class), any(HttpServletResponse.class)))
			.thenReturn(Map.of(
				JwtService.USER_ID, 1L,
				JwtService.LOGIN_ID, loginId,
				JwtService.ROLE, role
			));
	}
}