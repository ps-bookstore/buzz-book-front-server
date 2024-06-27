package store.buzzbook.front.service.cart;

import store.buzzbook.front.dto.cart.GetCartResponse;
import store.buzzbook.front.dto.cart.UpdateCartRequest;

public interface CartService {
	GetCartResponse getCartByCartId(Long cartId);
	void deleteCartDetail(Long detailId);
	void updateCart(Long detailId, Integer quantity);
}
