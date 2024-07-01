package store.buzzbook.front.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.user.UserService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mypage")
public class MyPageController {
	private final UserService userService;
	private final CookieUtils cookieUtils;

	@GetMapping
	public String myPage(Model model) {
		UserInfo userInfo = userService.getUserInfo();

		model.addAttribute("title", "마이페이지");
		model.addAttribute("page", "myinfo");
		model.addAttribute("user", userInfo);

		return "index";
	}

	@GetMapping("/deactivate")
	public String deactivateForm(Model model) {

		model.addAttribute("title", "탈퇴");
		model.addAttribute("page", "deactivate");

		return "index";
	}

	@GetMapping("/edit")
	public String editForm(Model model) {
		UserInfo userInfo = userService.getUserInfo();

		model.addAttribute("title", "정보 수정");
		model.addAttribute("page", "info-edit");
		model.addAttribute("user", userInfo);

		return "index";
	}

	@GetMapping("/password")
	public String changePasswordForm(Model model) {
		model.addAttribute("title", "비밀번호 변경");
		model.addAttribute("page", "change-password");

		return "index";
	}

	@PostMapping("/deactivate")
	public String deactivate(Model model, @ModelAttribute DeactivateUserRequest deactivateUserRequest, HttpServletRequest request, HttpServletResponse response) {
		userService.deactivate(deactivateUserRequest);
		
		//todo 로그아웃 처리
		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		return "redirect:/home";
	}

	@PostMapping("/edit")
	public String edit(Model model, @ModelAttribute UpdateUserRequest updateUserRequest) {
		UserInfo userInfo = userService.updateUserInfo(updateUserRequest);

		model.addAttribute("user", userInfo);

		return "redirect:/mypage";
	}

	@PostMapping("/password")
	public String changePassword(Model model, ChangePasswordRequest changePasswordRequest) {

		userService.changePassword(changePasswordRequest);

		return "redirect:/mypage";
	}
}
