package store.buzzbook.front.service.cart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.client.cart.CartClient;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.cart.CartNotFoundException;
import store.buzzbook.front.common.exception.cart.InvalidCartUuidException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.CreateCartDetailRequest;
import store.buzzbook.front.service.cart.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private CartClient cartClient;

	@Mock
	private CookieUtils cookieUtils;

	@InjectMocks
	private CartServiceImpl cartService;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private String uuid;
	private Long userId;
	private Long detailId;
	private CreateCartDetailRequest createCartDetailRequest;
	private List<CartDetailResponse> cartDetailResponses;
	private Cookie wrappedCookie;


	@BeforeEach
	void setUp() {
		uuid = "sample-uuid";
		userId = 1L;
		detailId = 1L;
		createCartDetailRequest = new CreateCartDetailRequest(
			1, 2
		);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);

		CartDetailResponse cartDetailResponse = CartDetailResponse.builder()
			.id(1L)
			.price(1000)
			.canWrap(false)
			.categoryId(2)
			.productName("테스트 상품")
			.quantity(1)
			.thumbnailPath("/example").build();

		cartDetailResponses = new LinkedList<>();
		cartDetailResponses.add(cartDetailResponse);
		wrappedCookie = new Cookie(CookieUtils.COOKIE_CART_KEY, uuid);
	}
	@Test
	@DisplayName("유저id로 cart uuid 얻기 성공")
	void testGetUuidByUserIdSuccess() {
		when(cartClient.getUuidByUserId()).thenReturn(ResponseEntity.ok(uuid));

		String result = cartService.getUuidByUserId(userId);

		assertEquals(uuid, result);
		verify(cartClient, times(1)).getUuidByUserId();
	}

	@Test
	@DisplayName("유저id로 cart uuid 얻기 중 토큰 인증인가 실패")
	void testGetUuidByUserIdUnauthorized() {
		when(cartClient.getUuidByUserId()).thenThrow(FeignException.Unauthorized.class);

		assertThrowsExactly(AuthorizeFailException.class, () -> {
			cartService.getUuidByUserId(userId);
		});

		verify(cartClient, times(1)).getUuidByUserId();
	}

	@Test
	@DisplayName("cart 생성 및 uuid 쿠키 생성 성공")
	void testCreateCartAndSaveCookieSuccess() {
		when(cartClient.createCart()).thenReturn(ResponseEntity.ok(uuid));
		when(cookieUtils.wrapCartCookie(uuid)).thenReturn(wrappedCookie);

		String result = cartService.createCartAndSaveCookie(response);

		assertEquals(uuid, result);
		verify(response, times(1)).addCookie(any(Cookie.class));
		verify(cartClient, times(1)).createCart();
	}

	@Test
	@DisplayName("cart 생성 및 uuid 쿠키 생성 중 인증인가 실패")
	void testCreateCartAndSaveCookieForbidden() {
		when(cartClient.createCart()).thenThrow(FeignException.Forbidden.class);

		assertThrowsExactly(InvalidCartUuidException.class, () -> {
			cartService.createCartAndSaveCookie(response);
		});

		verify(cartClient, times(1)).createCart();
	}

	@Test
	@DisplayName("요청에서 cart 얻기 성공")
	void testGetCartByRequestSuccess() {
		when(request.getAttribute(CookieUtils.COOKIE_CART_KEY)).thenReturn(uuid);
		when(cartClient.getCartByUuid(uuid)).thenReturn(ResponseEntity.ok(cartDetailResponses));

		List<CartDetailResponse> result = cartService.getCartByRequest(request);

		assertEquals(cartDetailResponses, result);
		verify(cartClient, times(1)).getCartByUuid(anyString());
	}

	@Test
	@DisplayName("요청에서 cart 얻기 중 유효하지 않은 uuid")
	void testGetCartByRequestInvalidUuid() {
		when(request.getAttribute(CookieUtils.COOKIE_CART_KEY)).thenReturn(uuid);
		when(cartClient.getCartByUuid(uuid)).thenThrow(FeignException.Forbidden.class);

		assertThrowsExactly(InvalidCartUuidException.class, () -> {
			cartService.getCartByRequest(request);
		});

		verify(cartClient, times(1)).getCartByUuid(anyString());
	}

	@Test
	@DisplayName("uuid로 카트 요청에서 cart 얻기 성공")
	void testGetCartByUuidSuccess() {
		when(cartClient.getCartByUuid(uuid)).thenReturn(ResponseEntity.ok(cartDetailResponses));

		List<CartDetailResponse> result = cartService.getCartByUuid(uuid);

		assertEquals(cartDetailResponses, result);
		verify(cartClient, times(1)).getCartByUuid(uuid);
	}

	@Test
	@DisplayName("uuid로 카트 요청에서 cart 얻기 중 인증인가 실패")
	void testGetCartByUuidInvalidUuid() {
		when(cartClient.getCartByUuid(uuid)).thenThrow(FeignException.Forbidden.class);

		assertThrowsExactly(InvalidCartUuidException.class, () -> {
			cartService.getCartByUuid(uuid);
		});

		verify(cartClient, times(1)).getCartByUuid(uuid);
	}

	@Test
	@DisplayName("uuid로 카트 요청에서 cart 얻기 중 잘못된 정보 전달")
	void testGetCartByUuidBadRequest() {
		when(cartClient.getCartByUuid(uuid)).thenThrow(FeignException.BadRequest.class);

		assertThrowsExactly(CartNotFoundException.class, () -> {
			cartService.getCartByUuid(uuid);
		});

		verify(cartClient, times(1)).getCartByUuid(uuid);
	}

	@Test
	@DisplayName("카트 상세 삭제 성공")
	void testDeleteCartDetailSuccess() {
		when(cartClient.deleteCartDetail(uuid, detailId)).thenReturn(ResponseEntity.ok(cartDetailResponses));

		List<CartDetailResponse> result = cartService.deleteCartDetail(uuid, detailId);

		assertEquals(cartDetailResponses, result);
		verify(cartClient, times(1)).deleteCartDetail(anyString(), anyLong());
	}

	@Test
	@DisplayName("카트 상세 삭제 중 유효하지 않은 uuid")
	void testDeleteCartDetailInvalidUuid() {
		when(cartClient.deleteCartDetail(uuid, detailId)).thenThrow(FeignException.Forbidden.class);

		assertThrowsExactly(InvalidCartUuidException.class, () -> {
			cartService.deleteCartDetail(uuid, detailId);
		});

		verify(cartClient, times(1)).deleteCartDetail(anyString(), anyLong());
	}

	@Test
	@DisplayName("카트 상세 삭제 중 잘못된 요청")
	void testDeleteCartDetailBadRequest() {
		when(cartClient.deleteCartDetail(uuid, detailId)).thenThrow(FeignException.BadRequest.class);

		assertThrowsExactly(CartNotFoundException.class, () -> {
			cartService.deleteCartDetail(uuid, detailId);
		});

		verify(cartClient, times(1)).deleteCartDetail(anyString(), anyLong());
	}

	@Test
	@DisplayName("카트 상세 수정 성공")
	void testUpdateCartSuccess() {
		when(cartClient.updateCartDetail(uuid, detailId, createCartDetailRequest.quantity())).thenReturn(ResponseEntity.ok().build());;

		cartService.updateCart(uuid, detailId, createCartDetailRequest.quantity());

		verify(cartClient, times(1)).updateCartDetail(anyString(), anyLong(), anyInt());
	}

	@Test
	@DisplayName("카트 상세 수정 중 유효하지 않은 uuid")
	void testUpdateCartInvalidUuid() {
		doThrow(FeignException.Forbidden.class).when(cartClient).updateCartDetail(uuid, detailId, createCartDetailRequest.quantity());

		assertThrowsExactly(InvalidCartUuidException.class, () -> {
			cartService.updateCart(uuid, detailId, createCartDetailRequest.quantity());
		});

		verify(cartClient, times(1)).updateCartDetail(anyString(), anyLong(), anyInt());
	}

	@Test
	@DisplayName("카트 상세 수정 중 잘못된 요청")
	void testUpdateCartBadRequest() {
		doThrow(FeignException.BadRequest.class).when(cartClient).updateCartDetail(uuid, detailId, createCartDetailRequest.quantity());

		assertThrowsExactly(CartNotFoundException.class, () -> {
			cartService.updateCart(uuid, detailId, createCartDetailRequest.quantity());
		});

		verify(cartClient, times(1)).updateCartDetail(anyString(), anyLong(), anyInt());
	}

	@Test
	@DisplayName("카트 상세 전부 삭제")
	void testDeleteAll_Success() {
		when(cartClient.deleteAllCartDetail(uuid)).thenReturn(ResponseEntity.ok().build());;

		cartService.deleteAll(uuid);

		verify(cartClient, times(1)).deleteAllCartDetail(anyString());
	}

	@Test
	@DisplayName("카트 상세 전부 삭제 중 유효하지 않은 uuid")
	void testDeleteAll_InvalidUuid() {
		doThrow(FeignException.Forbidden.class).when(cartClient).deleteAllCartDetail(uuid);

		assertThrowsExactly(InvalidCartUuidException.class, () -> {
			cartService.deleteAll(uuid);
		});

		verify(cartClient, times(1)).deleteAllCartDetail(anyString());
	}

	@Test
	@DisplayName("카트 상세 전부 삭제 중 잘못된 요청")
	void testDeleteAllBadRequest() {
		doThrow(FeignException.BadRequest.class).when(cartClient).deleteAllCartDetail(uuid);

		assertThrowsExactly(CartNotFoundException.class, () -> {
			cartService.deleteAll(uuid);
		});

		verify(cartClient, times(1)).deleteAllCartDetail(anyString());
	}

	@Test
	@DisplayName("요청에서 cart uuid 얻기 성공")
	void testGetCartIdFromRequestSuccess() {
		when(request.getAttribute(CookieUtils.COOKIE_CART_KEY)).thenReturn(uuid);

		String result = cartService.getCartIdFromRequest(request);

		assertEquals(uuid, result);
	}

	@Test
	@DisplayName("요청에서 cart uuid 얻기 실패")
	void testGetCartIdFromRequestNotFound() {
		when(request.getAttribute(CookieUtils.COOKIE_CART_KEY)).thenReturn(null);

		assertThrowsExactly(UnknownApiException.class, () -> {
			cartService.getCartIdFromRequest(request);
		});
	}

	@Test
	@DisplayName("새로운 카트 상세 만들기 성공")
	void testCreateCartDetailSuccess() {
		when(cartClient.createCartDetail(uuid, createCartDetailRequest)).thenReturn(ResponseEntity.ok().build());

		cartService.createCartDetail(uuid, createCartDetailRequest);

		verify(cartClient, times(1)).createCartDetail(anyString(), any(CreateCartDetailRequest.class));
	}

	@Test
	@DisplayName("새로운 카트 상세 만들기 중 잘못된 요청")
	void testCreateCartDetailBadRequest() {
		doThrow(FeignException.BadRequest.class).when(cartClient).createCartDetail(uuid, createCartDetailRequest);

		assertThrows(UnknownApiException.class, () -> {
			cartService.createCartDetail(uuid, createCartDetailRequest);
		});

		verify(cartClient, times(1)).createCartDetail(anyString(), any(CreateCartDetailRequest.class));
	}
}