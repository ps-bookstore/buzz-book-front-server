package store.buzzbook.front.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.jwt.JwtClient;
import store.buzzbook.front.common.annotation.JwtValidate;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.UserService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class DeactivateRestController {
	private final UserService userService;
	private final JwtClient jwtClient;
	private final CookieUtils cookieUtils;

	@JwtValidate
	@PostMapping("/mypage/deactivate")
	public ResponseEntity<Void> deactivate(@RequestBody DeactivateUserRequest deactivateUserRequest, HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(JwtService.USER_ID);
		userService.deactivate(userId, deactivateUserRequest);

		return ResponseEntity.ok().build();
	}
}
