package store.buzzbook.front.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.user.ActivateRequest;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.UserService;

@RequiredArgsConstructor
@RestController
public class ActivateRestController {
	private final JwtService jwtService;
	private final UserService userService;

	@PostMapping("/activate")
	public ResponseEntity<Void> activateUser(@RequestBody ActivateRequest activateRequest) {
		String loginId = jwtService.checkDormantTokenAndCode(activateRequest.token(), activateRequest.code());

		userService.activate(loginId);
		return ResponseEntity.ok().build();
	}
}
