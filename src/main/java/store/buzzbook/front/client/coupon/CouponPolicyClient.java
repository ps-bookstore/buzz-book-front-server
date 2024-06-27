package store.buzzbook.front.client.coupon;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.dto.coupon.CreateCouponPolicyRequest;
import store.buzzbook.front.dto.coupon.CreateCouponPolicyResponse;

@FeignClient(name = "couponClient", url = "http://localhost:8091/api/coupons/policies")
public interface CouponPolicyClient {

	@GetMapping
	Page<CouponPolicyResponse> getCouponPoliciesByPaging(Pageable pageable);

	@PostMapping
	CreateCouponPolicyResponse createCouponPolicy(CreateCouponPolicyRequest request);

	@GetMapping("/types")
	List<CouponTypeResponse> getCouponTypes();

}
