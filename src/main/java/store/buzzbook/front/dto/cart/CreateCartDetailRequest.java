package store.buzzbook.front.dto.cart;

import jakarta.validation.constraints.NotNull;

public record CreateCartDetailRequest(
	@NotNull
	Integer quantity,
	@NotNull
	Integer productId
) {
}
