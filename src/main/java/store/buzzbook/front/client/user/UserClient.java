package store.buzzbook.front.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.front.common.interceptor.FeignInterceptor;
import store.buzzbook.front.dto.coupon.DownloadCouponRequest;
import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserApiRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;

@FeignClient(name = "userClient", url = "http://${api.gateway.host}:"
	+ "${api.gateway.port}/api/account", configuration = {FeignInterceptor.class})
public interface UserClient {

	@PostMapping("/register")
	ResponseEntity<RegisterUserResponse> registerUser(@RequestBody RegisterUserApiRequest registerUserApiRequest);

	@PostMapping("/login")
	ResponseEntity<LoginUserResponse> requestLogin(@RequestBody String loginId);

	@PutMapping("/login")
	ResponseEntity<UserInfo> successLogin(@RequestBody String loginId);

	// -- mypage --
	@GetMapping("/mypage")
	ResponseEntity<UserInfo> getUserInfo();

	@PutMapping("/mypage/password")
	ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest);

	@PutMapping("/mypage")
	ResponseEntity<UserInfo> updateUser(@RequestBody UpdateUserRequest updateUserRequest);

	@PutMapping("/mypage/deactivate")
	ResponseEntity<Void> deactivateUser(@RequestBody DeactivateUserRequest deactivateUserRequest);

	@PostMapping("/coupons/download")
	public void downloadCoupon(@RequestBody DownloadCouponRequest request);
}
