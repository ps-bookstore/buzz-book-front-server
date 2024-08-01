package store.buzzbook.front.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.OauthRegisterRequest;

@FeignClient(name = "userAuthClient", url = "http://${api.gateway.host}:"
	+ "${api.gateway.port}/api/oauth2")
public interface UserAuthClient {

	@GetMapping("/register")
	ResponseEntity<Boolean> isRegistered(@RequestParam String provideId, @RequestParam String provider);
	@GetMapping("/login")
	ResponseEntity<LoginUserResponse> requestLogin(@RequestParam String provideId, @RequestParam String provider);
	@PostMapping("/register")
	ResponseEntity<Void> registerUser(@RequestBody OauthRegisterRequest registerRequest);
}
