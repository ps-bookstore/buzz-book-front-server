package store.buzzbook.front.controller.admin.coupon;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class CouponTypeController {
	@GetMapping("/coupon-type")
	public String couponType(Model model) {
		model.addAttribute("page", "couponType");
		return "admin/index";
	}
}
