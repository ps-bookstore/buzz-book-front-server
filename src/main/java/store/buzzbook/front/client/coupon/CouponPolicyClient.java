package store.buzzbook.front.client.coupon;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.front.dto.coupon.CouponPolicyConditionRequest;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.dto.coupon.CreateCouponPolicyRequest;
import store.buzzbook.front.dto.coupon.CreateCouponPolicyResponse;

@FeignClient(name = "couponClient", url = "http://${api.coupon.host}:" + "${api.coupon.port}/api/coupons/policies")
public interface CouponPolicyClient {

	@PostMapping("/condition")
	Page<CouponPolicyResponse> getCouponPoliciesByPaging(
		@RequestBody CouponPolicyConditionRequest condition);

	@PostMapping
	CreateCouponPolicyResponse createCouponPolicy(CreateCouponPolicyRequest request);

	@GetMapping("/types")
	List<CouponTypeResponse> getCouponTypes();

	@GetMapping("/specifics/{bookId}")
	List<CouponPolicyResponse> getSpecificCouponPolicies(@PathVariable int bookId);
}
