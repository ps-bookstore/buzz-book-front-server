package store.buzzbook.front.dto.cart;

import jakarta.validation.constraints.NotNull;

public record DeleteCartDetailRequest(
	@NotNull
	Long cartId,
	@NotNull
	Long productId
) {
}
