package store.buzzbook.front.controller.user;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class LoginController {

	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error, Model model) {
		log.info("get Login");
		if (error != null) {
			model.addAttribute("error", "아이디나 비밀번호를 확인해주세요.");
		}
		return "pages/register/login";
	}

	@GetMapping("/auth/login/wait")
	public String loginWait() {
		return "pages/register/login-wait";
	}

}
