package store.buzzbook.front.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.Operation;
import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserApiRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;

@FeignClient(name = "userClient", url = "http://${api.core.host}:" + "${api.core.port}/api/account")
public interface UserClient {

	@PostMapping("/register")
	ResponseEntity<RegisterUserResponse> registerUser(@RequestBody RegisterUserApiRequest registerUserApiRequest);

	@PostMapping("/login")
	ResponseEntity<LoginUserResponse> requestLogin(@RequestBody String loginId);

	@PutMapping("/login")
	ResponseEntity<UserInfo> successLogin(@RequestBody String loginId);

	// -- mypage --
	@GetMapping("/mypage")
	ResponseEntity<UserInfo> getUserInfo(Long userId);

	@PutMapping("/mypage/password")
	ResponseEntity<Void> changePassword(Long userId, @RequestBody ChangePasswordRequest changePasswordRequest);

	@PutMapping("/mypage")
	ResponseEntity<UserInfo> updateUser(Long userId, @RequestBody UpdateUserRequest updateUserRequest);

	@PutMapping("/mypage/deactivate")
	ResponseEntity<Void> deactivateUser(Long userId, @RequestBody DeactivateUserRequest deactivateUserRequest);
}
