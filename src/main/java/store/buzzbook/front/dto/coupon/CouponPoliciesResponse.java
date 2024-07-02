package store.buzzbook.front.dto.coupon;

import java.util.List;

import lombok.Builder;

@Builder
public record CouponPoliciesResponse(
	List<CouponPolicyResponse> globalCouponPolicies,
	List<CouponPolicyResponse> specificCouponPolicies,
	List<CouponPolicyResponse> categoryCouponPolicies
) {
}
