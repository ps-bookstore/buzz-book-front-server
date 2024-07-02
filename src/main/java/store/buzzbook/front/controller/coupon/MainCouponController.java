package store.buzzbook.front.controller.coupon;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.user.UserClient;
import store.buzzbook.front.dto.coupon.DownloadCouponRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class MainCouponController {

	private final UserClient userClient;

	@PostMapping("/download-coupon")
	public ResponseEntity<Void> downloadSpecificCoupon(@RequestBody Map<String, Integer> request) {
		int couponPolicyId = request.get("couponPolicyId");

		try {
			userClient.downloadCoupon(DownloadCouponRequest.create(1L, couponPolicyId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.ok().build();
	}
}
