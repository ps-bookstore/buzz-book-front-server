package store.buzzbook.front.controller.user;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.entity.register.LoginForm;
import store.buzzbook.front.jwt.JWTUtil;

@Controller
@Slf4j
public class LoginController {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;

	public LoginController(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}


	@GetMapping("/login")
	public String login() {
		return "pages/register/login";
	}

	@PostMapping("/login")
	public String loginSubmit(@ModelAttribute LoginForm form, HttpServletResponse response, Model model) {
		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(form.getId(), form.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			String username = form.getId();
			String role = authentication.getAuthorities().iterator().next().getAuthority();
			String token = jwtUtil.createJwt(username, role, 60 * 60 * 1000L); // 1시간 유효 토큰 생성
			String refreshToken = jwtUtil.createRefreshToken(username, 7 * 24 * 60 * 60 * 1000L); // 7일 유효 리프레시 토큰 생성

			Cookie tokenCookie = new Cookie("jwtToken", token);
			tokenCookie.setHttpOnly(true);
			// tokenCookie.setSecure(true); // HTTPS에서만 사용
			tokenCookie.setPath("/");
			tokenCookie.setMaxAge(60 * 60); // 1시간

			Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
			refreshTokenCookie.setHttpOnly(true);
			// refreshTokenCookie.setSecure(true); // HTTPS에서만 사용
			refreshTokenCookie.setPath("/");
			refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일

			response.addCookie(tokenCookie);
			response.addCookie(refreshTokenCookie);

			return "redirect:/home";
		} catch (AuthenticationException e) {
			log.info("Authentication failed: {}", e.getMessage());
			model.addAttribute("error", "로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.");
			return "redirect:/login";
		}
	}

}
