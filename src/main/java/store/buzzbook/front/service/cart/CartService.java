package store.buzzbook.front.service.cart;

import store.buzzbook.front.dto.cart.GetCartResponse;

public interface CartService {
	GetCartResponse getCartByCartId(Long cartId);
	GetCartResponse deleteCartDetail(Long cartId,Long detailId);
	GetCartResponse updateCart(Long detailId, Integer quantity, Long cartId);
	void deleteAll(Long cartId);
}
