package store.buzzbook.front.controller.admin.point;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.point.PointClient;
import store.buzzbook.front.dto.point.CreatePointPolicyRequest;
import store.buzzbook.front.dto.point.PointPolicyResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/points")
public class AdminPointController {

	private final PointClient pointClient;

	@GetMapping
	public String getPointPolicies(Model model) {
		List<PointPolicyResponse> policies = pointClient.getPointPolicies();

		model.addAttribute("pointPolicies", policies);
		model.addAttribute("page", "point-manage");

		return "admin/index";
	}

	@GetMapping("/policies")
	public String createPointPolicy(Model model) {
		model.addAttribute("page", "create-point-policy");
		return "admin/index";
	}

	@PostMapping("/policies")
	public String createPointPolicy(@ModelAttribute CreatePointPolicyRequest request) {
		pointClient.createPointPolicy(request);

		return "redirect:/admin/points";
	}

}
