package store.buzzbook.front.client.jwt;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import store.buzzbook.front.dto.jwt.AuthRequest;
import store.buzzbook.front.dto.jwt.JwtResponse;
import store.buzzbook.front.service.jwt.JwtService;

@FeignClient(name = "jwtClient", url = "http://${api.core.host}" + ":${api.core.port}/api")
public interface JwtClient {
	@PostMapping("/auth/token")
	ResponseEntity<JwtResponse> authToken(@RequestBody AuthRequest authRequest);
	@GetMapping("/auth/info")
	ResponseEntity<Map<String, Object>> getUserInfo(
		@RequestHeader(value = JwtService.TOKEN_HEADER, required = false) String accessToken,
		@RequestHeader(value = JwtService.REFRESH_HEADER, required = false) String refreshToken);

}
