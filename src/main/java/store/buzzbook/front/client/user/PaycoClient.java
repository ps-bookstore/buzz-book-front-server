package store.buzzbook.front.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.dto.user.PaycoAuthResponse;
import store.buzzbook.front.dto.user.PaycoLogoutResponse;

@FeignClient(name = "paycoClient", url = "https://id.payco.com/oauth2.0")
public interface PaycoClient {
	@GetMapping("/token")
	ResponseEntity<PaycoAuthResponse> requestToken(@RequestParam("grant_type") String grantType,
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("code") String code);

	@GetMapping("/token")
	ResponseEntity<PaycoAuthResponse> refreshToken(@RequestParam("grant_type") String grantType,
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("refresh_token") String refreshToken);

	@GetMapping("/logout")
	ResponseEntity<PaycoLogoutResponse> logout(
		@RequestParam("client_id") String clientId,
		@RequestParam("token") String accessToken,
		@RequestParam("client_secret") String clientSecret);
}
