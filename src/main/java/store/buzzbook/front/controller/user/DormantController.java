package store.buzzbook.front.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.service.jwt.JwtService;

@RequiredArgsConstructor
@Controller
public class DormantController {
	private final JwtService jwtService;

	@GetMapping("/activate")
	public String activateForm(@RequestParam String token, Model model) {
		jwtService.existsDormantToken(token);

		model.addAttribute("title", "계정 활성화");
		model.addAttribute("page", "activate");
		model.addAttribute("token", token);

		return "index";
	}
}
