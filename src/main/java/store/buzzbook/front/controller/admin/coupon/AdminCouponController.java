package store.buzzbook.front.controller.admin.coupon;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.dto.coupon.CreateCouponPolicyRequest;
import store.buzzbook.front.service.coupon.AdminCouponService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
public class AdminCouponController {

	private final AdminCouponService adminCouponService;

	@GetMapping
	public String couponManage(
		@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
		@RequestParam(defaultValue = "ALL") String discountTypeName,
		@RequestParam(defaultValue = "ALL") String isDeleted,
		@RequestParam(defaultValue = "ALL") String couponTypeName,
		Model model) {

		Page<CouponPolicyResponse> couponPolicies = adminCouponService.getCouponPolicies(pageable, discountTypeName,
			isDeleted, couponTypeName);
		List<CouponTypeResponse> couponTypes = adminCouponService.getCouponTypes();

		model.addAttribute("couponTypes", couponTypes);
		model.addAttribute("couponPolicies", couponPolicies);
		model.addAttribute("discountType", discountTypeName);
		model.addAttribute("isDeleted", isDeleted);
		model.addAttribute("couponType", couponTypeName);
		model.addAttribute("page", "couponManage");

		return "admin/index";
	}

	@GetMapping("/policies")
	public String createCouponPolicy(Model model) {
		List<CouponTypeResponse> couponTypes = adminCouponService.getCouponTypes();

		model.addAttribute("couponTypes", couponTypes);
		model.addAttribute("page", "couponPolicy");

		return "admin/index";
	}

	@PostMapping("/policies")
	public String createCouponPolicy(@Valid @ModelAttribute CreateCouponPolicyRequest request,
		BindingResult bindingResult) throws BadRequestException {

		if (bindingResult.hasErrors()) {
			throw new BadRequestException();
		}

		adminCouponService.createCouponPolicy(request);

		return "redirect:/admin/coupons";
	}

}
