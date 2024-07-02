package store.buzzbook.front.controller.home;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.coupon.CouponPolicyClient;
import store.buzzbook.front.client.user.UserClient;
import store.buzzbook.front.dto.coupon.CouponPoliciesResponse;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final UserClient userClient;
	private final CouponPolicyClient couponPolicyClient;

	@GetMapping("home")
	public String home(Model model) {
		CouponPoliciesResponse couponPolicies = couponPolicyClient.getCouponPoliciesByScope(
			List.of("global", "book", "category"));

		model.addAttribute("page", "main");
		model.addAttribute("title", "메인페이지");
		model.addAttribute("couponPolicies", couponPolicies);
		return "index";
	}
}
