package store.buzzbook.front.client.jwt;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.front.dto.jwt.AuthRequest;
import store.buzzbook.front.dto.jwt.JwtResponse;

@FeignClient(name = "jwtClient", url = "http://localhost:8100/api")
public interface JwtClient {
	@PostMapping("/auth/token")
	ResponseEntity<JwtResponse> authToken(@RequestBody AuthRequest authRequest);
}
