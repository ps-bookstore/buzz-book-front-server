package store.buzzbook.front.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateAddressRequest(
	@NotNull
	String address,
	@NotNull
	String detail,
	@Min(0) @Max(99999)
	@NotNull
	Integer zipcode,
	@NotNull
	String nation,
	@NotNull
	String alias
) {
}
