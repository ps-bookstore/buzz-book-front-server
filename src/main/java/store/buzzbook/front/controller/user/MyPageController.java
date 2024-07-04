package store.buzzbook.front.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.annotation.JwtValidate;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.UserService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mypage")
public class MyPageController {
	private final UserService userService;
	private final CookieUtils cookieUtils;

	@JwtValidate
	@GetMapping
	public String myPage(Model model, HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(JwtService.USER_ID);

		UserInfo userInfo = userService.getUserInfo(userId);

		model.addAttribute("title", "마이페이지");
		model.addAttribute("page", "myinfo");
		model.addAttribute("user", userInfo);

		return "index";
	}

	@JwtValidate
	@GetMapping("/deactivate")
	public String deactivateForm(Model model) {
		model.addAttribute("title", "탈퇴");
		model.addAttribute("page", "deactivate");

		return "index";
	}

	@JwtValidate
	@GetMapping("/edit")
	public String editForm(Model model, HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(JwtService.USER_ID);
		UserInfo userInfo = userService.getUserInfo(userId);

		model.addAttribute("title", "정보 수정");
		model.addAttribute("page", "info-edit");
		model.addAttribute("user", userInfo);

		return "index";
	}

	@JwtValidate
	@GetMapping("/password")
	public String changePasswordForm(Model model) {
		model.addAttribute("title", "비밀번호 변경");
		model.addAttribute("page", "change-password");

		return "index";
	}

	@JwtValidate
	@PostMapping("/deactivate")
	public String deactivate(@ModelAttribute DeactivateUserRequest deactivateUserRequest, HttpServletRequest request,
		HttpServletResponse response) {
		Long userId = (Long)request.getAttribute(JwtService.USER_ID);
		userService.deactivate(userId, deactivateUserRequest);

		//todo 로그아웃 처리
		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		return "redirect:/home";
	}

	@JwtValidate
	@PostMapping("/edit")
	public String edit(HttpServletRequest request, Model model, @ModelAttribute UpdateUserRequest updateUserRequest) {
		Long userId = (Long)request.getAttribute(JwtService.USER_ID);
		UserInfo userInfo = userService.updateUserInfo(userId, updateUserRequest);

		model.addAttribute("user", userInfo);

		return "redirect:/mypage";
	}

	@JwtValidate
	@PostMapping("/password")
	public String changePassword(HttpServletRequest request, ChangePasswordRequest changePasswordRequest) {
		Long userId = (Long)request.getAttribute(JwtService.USER_ID);
		userService.changePassword(userId, changePasswordRequest);

		return "redirect:/mypage";
	}

	@JwtValidate
	@GetMapping("/coupons")
	public String coupons(@RequestParam(defaultValue = "all") String couponStatusName, Model model) {
		model.addAttribute("coupons", userService.getUserCoupons(couponStatusName));
		model.addAttribute("page", "mypage-coupon");
		return "index";
	}
}
