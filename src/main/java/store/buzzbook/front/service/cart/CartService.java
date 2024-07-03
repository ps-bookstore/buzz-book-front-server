package store.buzzbook.front.service.cart;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.CreateCartDetailRequest;

public interface CartService {

	String getUuidByUserId(Long userId);
	String createCartAndSaveCookie(HttpServletResponse response);
	List<CartDetailResponse> getCartByRequest(HttpServletRequest request);
	List<CartDetailResponse> getCartByUuid(String uuid);
	List<CartDetailResponse> deleteCartDetail(String uuid,Long detailId);
	void updateCart(String uuid, Long detailId, Integer quantity);
	void deleteAll(String uuid);
	String getCartIdFromRequest(HttpServletRequest request);
	void createCartDetail(String uuid,CreateCartDetailRequest createCartDetailRequest);
}
