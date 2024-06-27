package store.buzzbook.front.service.coupon;

import java.util.List;

import store.buzzbook.front.dto.coupon.CouponTypeResponse;

public interface AdminCouponService {

	List<CouponTypeResponse> getCouponTypes();
}
