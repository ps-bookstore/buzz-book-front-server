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
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.coupon.CouponPolicyClient;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.dto.coupon.CouponPolicyConditionRequest;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.dto.coupon.CreateCouponPolicyRequest;
import store.buzzbook.front.dto.product.CategoryResponse;
import store.buzzbook.front.dto.product.ProductResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
public class AdminCouponController {

	private final CouponPolicyClient couponPolicyClient;
	private final ProductClient productClient;

	@GetMapping
	public String couponManage(
		@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
		@RequestParam(defaultValue = "ALL") String discountTypeName,
		@RequestParam(defaultValue = "ALL") String isDeleted,
		@RequestParam(defaultValue = "ALL") String couponTypeName,
		Model model) {

		Page<CouponPolicyResponse> couponPolicies = couponPolicyClient.getCouponPoliciesByPaging(
			CouponPolicyConditionRequest.create(pageable, discountTypeName, isDeleted, couponTypeName));
		List<CouponTypeResponse> couponTypes = couponPolicyClient.getCouponTypes();

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
		List<CouponTypeResponse> couponTypes = couponPolicyClient.getCouponTypes();

		model.addAttribute("couponTypes", couponTypes);
		model.addAttribute("page", "couponPolicy");

		return "admin/index";
	}

	@GetMapping("/policies/product-search")
	@ResponseBody
	public List<ProductResponse> searchProducts(@RequestParam("query") String query) {
		return productClient.searchProductByName(query);
	}

	@GetMapping("/policies/category-search")
	@ResponseBody
	public List<CategoryResponse> searchCategories() {
		return productClient.getAllCategories();
	}

	@PostMapping("/policies")
	public String createCouponPolicy(@Valid @ModelAttribute CreateCouponPolicyRequest request,
		BindingResult bindingResult) throws BadRequestException {

		if (bindingResult.hasErrors()) {
			throw new BadRequestException();
		}

		couponPolicyClient.createCouponPolicy(request);

		return "redirect:/admin/coupons";
	}
}
