package store.buzzbook.front.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserApiRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.dto.user.UserInfo;

@FeignClient(name = "userClient", url = "http://localhost:8080/api/account")
public interface UserClient {

	@PostMapping("/register")
	public RegisterUserResponse registerUser(RegisterUserApiRequest registerUserApiRequest);

	@PostMapping("/login")
	public LoginUserResponse requestLogin(String loginId);

	@PatchMapping("/login")
	public UserInfo successLogin(String loginId);

}
