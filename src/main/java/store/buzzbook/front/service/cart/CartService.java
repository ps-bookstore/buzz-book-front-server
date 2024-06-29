package store.buzzbook.front.service.cart;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.dto.cart.GetCartResponse;

public interface CartService {

	Long createCartAndSaveCookie(HttpServletResponse response);
	Long getCartIdByUserId(Long userId);
	GetCartResponse getCartByRequest(HttpServletRequest request);
	GetCartResponse getCartByCartId(Long cartId);
	GetCartResponse deleteCartDetail(Long cartId,Long detailId);
	GetCartResponse updateCart(Long cartId, Long detailId, Integer quantity);
	void deleteAll(Long cartId);
}
