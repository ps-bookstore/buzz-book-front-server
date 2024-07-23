package store.buzzbook.front.common.interceptor;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.front.common.util.CookieUtils;

@ExtendWith(MockitoExtension.class)
class FeignInterceptorTest {
	@Mock
	private CookieUtils cookieUtils;

	@Mock
	private HttpServletRequest request;

	@Mock
	private RequestTemplate requestTemplate;

	@InjectMocks
	private FeignInterceptor feignInterceptor;

	private String jwtTokenValue;
	private String refreshTokenValue;

	@BeforeEach
	void setUp() {
		jwtTokenValue = "testJwt";
		refreshTokenValue = "testRefresh";
		MockitoAnnotations.openMocks(this);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Test
	void testApplyWithCookies() {
		Cookie jwtCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, jwtTokenValue);
		Cookie refreshCookie = new Cookie(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshTokenValue);

		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.of(refreshCookie));

		feignInterceptor.apply(requestTemplate);
	}

	@Test
	void testApplyWithoutCookies() {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.empty());
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.empty());

		feignInterceptor.apply(requestTemplate);

		verify(requestTemplate, never()).header(eq(CookieUtils.COOKIE_JWT_ACCESS_KEY), anyString());
		verify(requestTemplate, never()).header(eq(CookieUtils.COOKIE_JWT_REFRESH_KEY), anyString());
	}

	@Test
	void testApplyNullAttributes() {
		RequestContextHolder.resetRequestAttributes();

		feignInterceptor.apply(requestTemplate);

		verify(requestTemplate, never()).header(eq(CookieUtils.COOKIE_JWT_ACCESS_KEY), anyString());
		verify(requestTemplate, never()).header(eq(CookieUtils.COOKIE_JWT_REFRESH_KEY), anyString());
	}

}
