package store.buzzbook.front.service.coupon.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.coupon.CouponClient;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.service.coupon.AdminCouponService;

@Service
@RequiredArgsConstructor
public class AdminCouponServiceImpl implements AdminCouponService {

	private final CouponClient couponClient;

	@Override
	public List<CouponTypeResponse> getCouponTypes() {
		return couponClient.getCouponTypes();
	}
}
