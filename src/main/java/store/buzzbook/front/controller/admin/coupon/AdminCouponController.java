package store.buzzbook.front.controller.admin.coupon;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.dto.coupon.CreateCouponTypeRequest;
import store.buzzbook.front.service.coupon.AdminCouponService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
public class AdminCouponController {

	private final AdminCouponService adminCouponService;

	@GetMapping("/types")
	public String getCouponTypes(Model model) {
		List<CouponTypeResponse> couponTypes = adminCouponService.getCouponTypes();
		model.addAttribute("couponTypes", couponTypes);
		model.addAttribute("page", "couponType");
		return "admin/index";
	}

	@PostMapping("/admin/coupons/types")
	public String createCouponType(@ModelAttribute CreateCouponTypeRequest request,
		Model model) {
		
		return "redirect:/admin/coupons/types";
	}

}
