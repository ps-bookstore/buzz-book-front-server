package store.buzzbook.front.client.coupon;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import store.buzzbook.front.dto.coupon.CouponTypeResponse;

@FeignClient(name = "couponClient", url = "http://localhost:8091/api/coupons/policies")
public interface CouponClient {

	@GetMapping("/types")
	List<CouponTypeResponse> getCouponTypes();
}
