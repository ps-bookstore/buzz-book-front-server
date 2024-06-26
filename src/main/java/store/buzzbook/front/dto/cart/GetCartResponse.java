package store.buzzbook.front.dto.cart;

import java.util.List;

public record GetCartResponse(
	Long id,
	Long userId,
	List<CartDetailResponse> cartDetailList
) {
}
