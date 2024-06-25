package store.buzzbook.front.controller.user;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.entity.register.LoginForm;
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
	public String registerSubmit(@ModelAttribute RegisterUserRequest registerUserRequest, Model model) {
		log.info("회원가입 요청 id : {}", registerUserRequest.loginId());

		RegisterUserResponse registerUserResponse = userService.registerUser(registerUserRequest);

		if(Objects.equals(registerUserResponse.status(), HttpStatus.BAD_REQUEST.value())){
			log.info("회원가입 실패 : redirect 회원가입 페이지");
			return "redirect:/signup";
		}

		return "redirect:/login";
	}


}
