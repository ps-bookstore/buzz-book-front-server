package store.buzzbook.front.dto.coupon;

import org.springframework.data.domain.Pageable;

public record CouponPolicyConditionRequest(

	Pageable pageable,
	String discountTypeName,
	String isDeleted,
	String couponTypeName
) {
	public static CouponPolicyConditionRequest create(Pageable pageable, String discountTypeName, String isDeleted,
		String couponTypeName) {
		return new CouponPolicyConditionRequest(pageable, discountTypeName, isDeleted, couponTypeName);
	}
}
