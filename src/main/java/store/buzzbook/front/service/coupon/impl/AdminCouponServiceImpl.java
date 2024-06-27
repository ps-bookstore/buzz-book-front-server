package store.buzzbook.front.service.coupon.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.coupon.CouponPolicyClient;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.dto.coupon.CreateCouponPolicyRequest;
import store.buzzbook.front.service.coupon.AdminCouponService;

@Service
@RequiredArgsConstructor
public class AdminCouponServiceImpl implements AdminCouponService {

	private final CouponPolicyClient couponPolicyClient;

	@Override
	public Page<CouponPolicyResponse> getCouponPolicies(Pageable pageable) {
		return couponPolicyClient.getCouponPoliciesByPaging(pageable);
	}

	@Override
	public List<CouponTypeResponse> getCouponTypes() {
		return couponPolicyClient.getCouponTypes();
	}

	@Override
	public void createCouponPolicy(CreateCouponPolicyRequest request) {
		couponPolicyClient.createCouponPolicy(request);
	}
}
