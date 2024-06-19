package store.buzzbook.front.controller.register;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.entity.register.LoginForm;
import store.buzzbook.front.entity.register.RegisterForm;

@Controller
@Slf4j
@RequestMapping("/register")
public class RegisterController {

	@GetMapping("/signup")
	public String registerForm(Model model) {
		model.addAttribute("registerForm", new RegisterForm());
		return "pages/register/signup";
	}

	@PostMapping("/signup")
	public String registerSubmit(@ModelAttribute RegisterForm form, Model model) {
		// 여기에 회원가입 로직을 추가합니다
		// form 객체에서 데이터를 가져와서 처리합니다
		model.addAttribute("registerForm", form);
		log.info("RegisterForm: {}", form);
		return "pages/register/login";
	}

	@GetMapping("/login")
	public String login() {
		return "pages/register/login";
	}

	@PostMapping("/login")
	public String loginSubmit(@ModelAttribute LoginForm form, Model model) {
		if(1 == 1) {
			model.addAttribute("error", "로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.");
			return "pages/register/login";
		}
		return "redirect:/home";
	}

}
