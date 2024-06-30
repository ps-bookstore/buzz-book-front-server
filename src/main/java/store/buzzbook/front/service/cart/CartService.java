package store.buzzbook.front.service.cart;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.GetCartResponse;

public interface CartService {

	Long createCartAndSaveCookie(HttpServletResponse response);
	Long getCartIdByUserId(Long userId);
	List<CartDetailResponse> getCartByRequest(HttpServletRequest request);
	List<CartDetailResponse> getCartByCartId(Long cartId);
	List<CartDetailResponse> deleteCartDetail(Long cartId,Long detailId);
	List<CartDetailResponse> updateCart(Long cartId, Long detailId, Integer quantity);
	void deleteAll(Long cartId);
}
