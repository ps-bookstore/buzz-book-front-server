package store.buzzbook.front.common.interceptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

	@Spy
	private RequestTemplate requestTemplate;

	@InjectMocks
	private FeignInterceptor feignInterceptor;

	private String jwtTokenValue;
	private String refreshTokenValue;

	@BeforeEach
	void setUp() {
		jwtTokenValue = "testJwt";
		refreshTokenValue = "testRefresh";
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Test
	void testApplyWithCookies() {
		Cookie jwtCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, jwtTokenValue);
		Cookie refreshCookie = new Cookie(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshTokenValue);

		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.of(refreshCookie));

		feignInterceptor.apply(requestTemplate);

		verify(cookieUtils, times(2)).getCookie(any(HttpServletRequest.class), anyString());

		String resultJwt = requestTemplate.headers().get(CookieUtils.COOKIE_JWT_ACCESS_KEY).stream().findFirst().orElse(null);
		String resultRefresh = requestTemplate.headers().get(CookieUtils.COOKIE_JWT_REFRESH_KEY).stream().findFirst().orElse(null);

		assertNotNull(resultJwt);
		assertNotNull(resultRefresh);
		assertEquals(jwtTokenValue,resultJwt);
		assertEquals(refreshTokenValue, resultRefresh);
	}

	@Test
	void testApplyWithoutCookies() {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.empty());
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.empty());

		feignInterceptor.apply(requestTemplate);

		verify(requestTemplate, never()).header(eq(CookieUtils.COOKIE_JWT_ACCESS_KEY), anyString());
		verify(requestTemplate, never()).header(eq(CookieUtils.COOKIE_JWT_REFRESH_KEY), anyString());

		assertEquals(0,requestTemplate.headers().size());
	}

	@Test
	void testApplyNullAttributes() {
		RequestContextHolder.resetRequestAttributes();

		feignInterceptor.apply(requestTemplate);

		verify(requestTemplate, never()).header(eq(CookieUtils.COOKIE_JWT_ACCESS_KEY), anyString());
		verify(requestTemplate, never()).header(eq(CookieUtils.COOKIE_JWT_REFRESH_KEY), anyString());
		assertEquals(0,requestTemplate.headers().size());
	}

}
