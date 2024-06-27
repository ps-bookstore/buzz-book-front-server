package store.buzzbook.front.service.coupon;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.dto.coupon.CreateCouponPolicyRequest;

public interface AdminCouponService {

	Page<CouponPolicyResponse> getCouponPolicies(Pageable pageable);

	List<CouponTypeResponse> getCouponTypes();

	void createCouponPolicy(CreateCouponPolicyRequest request);
}
