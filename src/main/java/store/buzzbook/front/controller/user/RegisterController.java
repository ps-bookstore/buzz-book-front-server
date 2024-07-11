package store.buzzbook.front.controller.user;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.service.user.UserService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RegisterController {
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;

	@GetMapping("/signup")
	public String registerForm() {
		return "pages/register/signup";
	}

	@PostMapping("/signup")
	public String registerSubmit(@ModelAttribute RegisterUserRequest registerUserRequest) {
		log.info("회원가입 요청 id : {}", registerUserRequest.loginId());

		userService.registerUser(registerUserRequest);

		log.debug("회원가입 성공 리다이렉션");
		return "redirect:/welcome?id=" + registerUserRequest.loginId();
	}

	@GetMapping("/welcome")
	public String welcome(@RequestParam("id")String id, Model model) {
		model.addAttribute("id", id);
		return "pages/register/signup-success";
	}


}
