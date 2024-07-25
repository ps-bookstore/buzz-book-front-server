package store.buzzbook.front.common.interceptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.Cookie;
import store.buzzbook.front.common.exception.cart.InvalidCartUuidException;
import store.buzzbook.front.common.exception.user.UserTokenException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.service.cart.CartService;
import store.buzzbook.front.service.jwt.JwtService;


@ExtendWith(MockitoExtension.class)
class CartInterceptorTest {
	@Mock
	private CartService cartService;

	@Mock
	private JwtService jwtService;

	@Mock
	private CookieUtils cookieUtils;

	@InjectMocks
	private CartInterceptor cartInterceptor;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private String cartUuid;
	private Long userId;
	private Cookie cartCookie;

	@BeforeEach
	void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		cartUuid = UUID.randomUUID().toString().replaceAll("-", "");
		userId = 1L;
		cartCookie = new Cookie(CookieUtils.COOKIE_CART_KEY, cartUuid);
	}

	@Test
	void testPreHandleMemberWithJwt() throws Exception {
		Cookie jwtCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, cartUuid);
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(jwtService.getUserIdFromJwt(request, response)).thenReturn(userId);
		when(cookieUtils.getCartIdFromRequest(request)).thenReturn(Optional.empty());
		when(cartService.getUuidByUserId(userId)).thenReturn(cartUuid);
		when(cookieUtils.wrapCartCookie(cartUuid)).thenReturn(cartCookie);

		boolean result = cartInterceptor.preHandle(request, response, new Object());

		assertTrue(result);
		assertNotNull(response.getCookie(CookieUtils.COOKIE_CART_KEY));
		assertEquals(cartUuid, response.getCookie(CookieUtils.COOKIE_CART_KEY).getValue());
		verify(cookieUtils, times(1)).getCookie(any(), anyString());
		verify(cookieUtils, times(1)).getCartIdFromRequest(any());
		verify(cookieUtils, times(1)).wrapCartCookie(anyString());
		verify(cartService, times(1)).getUuidByUserId(anyLong());
	}

	@Test
	void testPreHandleMemberWithInvalidJwt() {
		Cookie jwtCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, cartUuid);
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(jwtService.getUserIdFromJwt(request, response)).thenReturn(null);
		when(cookieUtils.getCartIdFromRequest(request)).thenReturn(Optional.empty());

		assertThrowsExactly(UserTokenException.class,
			()->cartInterceptor.preHandle(request, response, new Object()));
	}

	@Test
	void testPreHandleMemberWithCartId() throws Exception {
		Cookie jwtCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, cartUuid);
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(jwtService.getUserIdFromJwt(request, response)).thenReturn(userId);
		when(cookieUtils.getCartIdFromRequest(request)).thenReturn(Optional.of(cartCookie));

		boolean result = cartInterceptor.preHandle(request, response, new Object());

		assertTrue(result);
		assertNotNull(request.getAttribute(CookieUtils.COOKIE_CART_KEY));
		assertEquals(cartUuid, request.getAttribute(CookieUtils.COOKIE_CART_KEY));
		verify(cookieUtils, times(1)).getCookie(any(), anyString());
		verify(cookieUtils, times(1)).getCartIdFromRequest(any());
	}

	@Test
	void testPreHandleGuestWithoutCartId() throws Exception {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.empty());
		when(cookieUtils.getCartIdFromRequest(request)).thenReturn(Optional.empty());
		when(cartService.createCartAndSaveCookie(response)).thenReturn(cartUuid);

		boolean result = cartInterceptor.preHandle(request, response, new Object());

		assertTrue(result);
		assertEquals(cartUuid, request.getAttribute(CookieUtils.COOKIE_CART_KEY));
		verify(cartService).createCartAndSaveCookie(response);
	}

	@Test
	void testPreHandleGuestWithCartId() throws Exception {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.empty());
		when(cookieUtils.getCartIdFromRequest(request)).thenReturn(Optional.of(cartCookie));

		boolean result = cartInterceptor.preHandle(request, response, new Object());

		assertTrue(result);
		assertEquals(cartUuid, request.getAttribute(CookieUtils.COOKIE_CART_KEY));
	}

	@Test
	void testPreHandleGuestWithInvalidCartId() {
		cartCookie = new Cookie(CookieUtils.COOKIE_CART_KEY, "invalidCartId");
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.empty());
		when(cookieUtils.getCartIdFromRequest(request)).thenReturn(Optional.of(cartCookie));

		assertThrowsExactly(InvalidCartUuidException.class, () -> {
			cartInterceptor.preHandle(request, response, new Object());
		});
	}

	@Test
	void testPreHandleGuestWithValidCartId() throws Exception {
		cartCookie = new Cookie(CookieUtils.COOKIE_CART_KEY, cartUuid);
		cartCookie.setMaxAge(60 * 60 * 24 * 5);
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.empty());
		when(cookieUtils.getCartIdFromRequest(request)).thenReturn(Optional.of(cartCookie));

		boolean result = cartInterceptor.preHandle(request, response, new Object());

		assertTrue(result);
		assertEquals(cartUuid, request.getAttribute(CookieUtils.COOKIE_CART_KEY));
		assertNotNull(response.getCookie(CookieUtils.COOKIE_CART_KEY));
		assertTrue(Objects.requireNonNull(response.getCookie(CookieUtils.COOKIE_CART_KEY)).getMaxAge() > 60 * 60 * 24 * 5);
		verify(cookieUtils, times(1)).getCookie(any(), anyString());
		verify(cookieUtils, times(1)).getCartIdFromRequest(any());
	}

	@Test
	void testPreHandleGuestWithValidCartIdAndNoRefresh() throws Exception {
		cartCookie = new Cookie(CookieUtils.COOKIE_CART_KEY, cartUuid);
		cartCookie.setMaxAge(60 * 60 * 24 * 7);
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.empty());
		when(cookieUtils.getCartIdFromRequest(request)).thenReturn(Optional.of(cartCookie));

		boolean result = cartInterceptor.preHandle(request, response, new Object());

		assertTrue(result);
		assertEquals(cartUuid, request.getAttribute(CookieUtils.COOKIE_CART_KEY));
		assertNull(response.getCookie(CookieUtils.COOKIE_CART_KEY));
		verify(cookieUtils, times(1)).getCookie(any(), anyString());
		verify(cookieUtils, times(1)).getCartIdFromRequest(any());
	}
}
