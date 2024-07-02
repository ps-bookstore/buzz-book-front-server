package store.buzzbook.front.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateCartRequest(
	@NotNull
	long detailId,
	@NotNull
	int quantity
) {
}
