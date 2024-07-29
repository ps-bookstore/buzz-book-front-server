package store.buzzbook.front.common.aop;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.service.jwt.JwtService;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserJwtAopTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private CookieUtils cookieUtils;

	@Mock
	private JoinPoint joinPoint;

	@InjectMocks
	private UserJwtAop userJwtAop;

	private Cookie jwtCookie;
	private Cookie refreshCookie;
	private Map<String, Object> claims;

	@BeforeEach
	void setUp() {
		jwtCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, "jwtToken");
		refreshCookie = new Cookie(CookieUtils.COOKIE_JWT_REFRESH_KEY, "refreshToken");
		claims = Map.of(
			JwtService.USER_ID, 1,
			JwtService.LOGIN_ID, "testLoginId",
			JwtService.ROLE, "USER"
		);
	}

	@Test
	void testAuthenticateWithValidJwt() throws Throwable {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.of(refreshCookie));
		when(jwtService.getInfoMapFromJwt(request, response)).thenReturn(claims);

		userJwtAop.authenticate(joinPoint);

		verify(request).setAttribute(JwtService.USER_ID, ((Integer)claims.get(JwtService.USER_ID)).longValue());
		verify(request).setAttribute(JwtService.LOGIN_ID, claims.get(JwtService.LOGIN_ID));
		verify(request).setAttribute(JwtService.ROLE, claims.get(JwtService.ROLE));
	}

	@Test
	void testAuthenticateWithoutJWT() {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.empty());
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.empty());

		assertThrows(AuthorizeFailException.class, () -> {
			userJwtAop.authenticate(joinPoint);
		});
	}

	@Test
	void testAuthenticateWithoutAccessKey() {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.empty());
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.of(refreshCookie));

		assertThrows(AuthorizeFailException.class, () -> {
			userJwtAop.authenticate(joinPoint);
		});
	}

	@Test
	void testAuthenticateWithoutRefreshToken() {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.empty());

		assertThrows(AuthorizeFailException.class, () -> {
			userJwtAop.authenticate(joinPoint);
		});
	}

	@Test
	void testAuthenticateWithInvalidClaims1() throws Throwable {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.of(refreshCookie));
		when(jwtService.getInfoMapFromJwt(request, response)).thenReturn(Map.of());

		assertThrows(AuthorizeFailException.class, () -> {
			userJwtAop.authenticate(joinPoint);
		});
	}

}
