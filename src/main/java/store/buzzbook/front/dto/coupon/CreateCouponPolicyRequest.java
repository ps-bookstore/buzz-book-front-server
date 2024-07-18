package store.buzzbook.front.dto.coupon;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCouponPolicyRequest(

	@NotBlank(message = "쿠폰 정책의 이름은 공백이 불가합니다.")
	String name,

	@NotBlank(message = "쿠폰 정책의 할인 타입은 공백이 불가합니다.")
	String discountType,

	@DecimalMin(value = "0.0", inclusive = true, message = "할인율은 0보다 작을 수 없습니다.")
	@DecimalMax(value = "1.0", inclusive = true, message = "할인율은 1보다 클 수 없습니다.")
	Double discountRate,

	@Min(value = 0, message = "할인 금액은 0보다 커야 합니다.")
	Integer discountAmount,

	@Min(value = 0, message = "할인 기준 금액은 0보다 커야 합니다.")
	Integer standardPrice,

	@Min(value = 0, message = "최대 할인 금액은 0보다 커야 합니다.")
	Integer maxDiscountAmount,

	@Min(value = 0, message = "쿠폰 지속 날짜는 0보다 커야 합니다.")
	Integer period,

	@NotNull(message = "쿠폰 정책 다운로드 날짜는 null 이 아니어야 합니다.")
	String startDate,

	@NotNull(message = "쿠폰 정책 다운로드 날짜는 null 이 아니어야 합니다.")
	String endDate,

	@NotBlank(message = "쿠폰 정책 타입은 공백이 불가 합니다.")
	String couponType,

	@Min(value = 0, message = "id는 0보다 작을 수 없습니다.")
	Integer targetId
) {
}
