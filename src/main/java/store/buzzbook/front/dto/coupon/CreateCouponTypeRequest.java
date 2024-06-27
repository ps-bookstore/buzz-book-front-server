package store.buzzbook.front.dto.coupon;

import jakarta.validation.constraints.NotBlank;

public record CreateCouponTypeRequest(
	
	@NotBlank(message = "쿠폰 타입의 이름이 필요합니다.")
	String name
) {
}
