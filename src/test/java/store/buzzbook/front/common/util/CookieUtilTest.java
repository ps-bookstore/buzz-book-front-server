package store.buzzbook.front.common.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class CookieUtilTest {
	@Spy
	private CookieUtils cookieUtils;

	private HttpServletRequest request;
	private HttpServletResponse response;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
	}

	@Test
	void testGetCookie() {
		Cookie cookie = new Cookie(CookieUtils.COOKIE_CART_KEY, "testCartId");
		Cookie[] cookies = {cookie};
		when(request.getCookies()).thenReturn(cookies);

		Optional<Cookie> result = cookieUtils.getCookie(request, CookieUtils.COOKIE_CART_KEY);

		assertTrue(result.isPresent());
		assertEquals("testCartId", result.get().getValue());
	}

	@Test
	void testGetCookieShouldEmpty() {
		when(request.getCookies()).thenReturn(null);

		Optional<Cookie> result = cookieUtils.getCookie(request, CookieUtils.COOKIE_CART_KEY);

		assertTrue(result.isEmpty());
	}

	@Test
	void testGetCartIdFromRequest() {
		Cookie cookie = new Cookie(CookieUtils.COOKIE_CART_KEY, "testCartId");
		Cookie[] cookies = {cookie};
		when(request.getCookies()).thenReturn(cookies);

		Optional<Cookie> result = cookieUtils.getCartIdFromRequest(request);

		assertTrue(result.isPresent());
		assertEquals("testCartId", result.get().getValue());
	}

	@Test
	void testLogout() {
		Cookie accessCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, "testAccessToken");
		Cookie refreshCookie = new Cookie(CookieUtils.COOKIE_JWT_REFRESH_KEY, "testRefreshToken");
		Cookie cartCookie = new Cookie(CookieUtils.COOKIE_CART_KEY, "testCartId");
		Cookie[] cookies = {accessCookie, refreshCookie, cartCookie};
		when(request.getCookies()).thenReturn(cookies);

		cookieUtils.logout(request, response);

		verify(response, times(3)).addCookie(any(Cookie.class));
	}

	@Test
	void testDeleteCookie() {
		Cookie cookie = new Cookie(CookieUtils.COOKIE_CART_KEY, "testCartId");
		Cookie[] cookies = {cookie};
		when(request.getCookies()).thenReturn(cookies);

		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_CART_KEY);

		verify(response, times(1)).addCookie(any(Cookie.class));
	}

	@Test
	void testWrapCartCookie() {
		Cookie cookie = cookieUtils.wrapCartCookie("testCartId");

		assertNotNull(cookie);
		assertEquals(CookieUtils.COOKIE_CART_KEY, cookie.getName());
		assertEquals("testCartId", cookie.getValue());
		assertEquals("/", cookie.getPath());
		assertEquals(CookieUtils.DEFAULT_COOKIE_AGE, cookie.getMaxAge());
	}

	@Test
	void testWrapJwtTokenCookie() {
		String token = "Bearer testToken";
		Cookie cookie = cookieUtils.wrapJwtTokenCookie(token);

		assertNotNull(cookie);
		assertEquals(CookieUtils.COOKIE_JWT_ACCESS_KEY, cookie.getName());
		assertEquals("testToken", cookie.getValue());
		assertEquals("/", cookie.getPath());
		assertEquals(CookieUtils.DEFAULT_TOKEN_AGE, cookie.getMaxAge());
	}

	@Test
	void testWrapRefreshTokenCookie() {
		String token = "Bearer testToken";
		Cookie cookie = cookieUtils.wrapRefreshTokenCookie(token);

		assertNotNull(cookie);
		assertEquals(CookieUtils.COOKIE_JWT_REFRESH_KEY, cookie.getName());
		assertEquals("testToken", cookie.getValue());
		assertEquals("/", cookie.getPath());
		assertEquals(CookieUtils.DEFAULT_REFRESH_AGE, cookie.getMaxAge());
	}
}
