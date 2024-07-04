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
import store.buzzbook.front.common.annotation.JwtValidate;
import store.buzzbook.front.dto.point.CreatePointPolicyRequest;
import store.buzzbook.front.dto.point.DeletePointPolicyRequest;
import store.buzzbook.front.dto.point.PointPolicyResponse;
import store.buzzbook.front.dto.point.UpdatePointPolicyRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/points")
public class AdminPointController {

	private final PointClient pointClient;

	@JwtValidate
	@GetMapping
	public String getPointPolicies(Model model) {
		List<PointPolicyResponse> policies = pointClient.getPointPolicies();

		model.addAttribute("pointPolicies", policies);
		model.addAttribute("page", "point-manage");

		return "admin/index";
	}

	@JwtValidate
	@GetMapping("/policies")
	public String createPointPolicy(Model model) {
		model.addAttribute("page", "create-point-policy");
		return "admin/index";
	}

	@JwtValidate
	@PostMapping("/policies")
	public String createPointPolicy(@ModelAttribute CreatePointPolicyRequest request) {
		pointClient.createPointPolicy(request);

		return "redirect:/admin/points";
	}

	@JwtValidate
	@PostMapping("/policies/edit")
	public String updatePointPolicy(@ModelAttribute PointPolicyResponse request, Model model) {
		model.addAttribute("pointPolicy", request);
		model.addAttribute("page", "update-point-policy");

		return "admin/index";
	}

	@JwtValidate
	@PostMapping("/policies/edit/process")
	public String updatePointPolicyProcess(@ModelAttribute UpdatePointPolicyRequest request) {
		pointClient.updatePointPolicy(request);

		return "redirect:/admin/points";
	}

	@JwtValidate
	@PostMapping("/policies/delete")
	public String deletePointPolicy(@ModelAttribute DeletePointPolicyRequest request) {
		pointClient.deletePointPolicy(request);

		return "redirect:/admin/points";
	}

}
