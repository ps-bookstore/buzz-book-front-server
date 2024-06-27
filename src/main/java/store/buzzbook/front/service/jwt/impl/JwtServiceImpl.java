package store.buzzbook.front.service.jwt.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.jwt.JwtClient;
import store.buzzbook.front.dto.jwt.AuthRequest;
import store.buzzbook.front.dto.jwt.JwtResponse;
import store.buzzbook.front.service.jwt.JwtService;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
	private final JwtClient jwtClient;

	@Override
	public String accessToken(AuthRequest authRequest) {
		ResponseEntity<JwtResponse> response = jwtClient.authToken(authRequest);

		if (response.getStatusCode().is2xxSuccessful()) {
			String accessToken = response.getHeaders().getFirst("Authorization");
			if (accessToken != null && accessToken.startsWith("Bearer ")) {
				return accessToken.substring(7); // 'Bearer ' 부분을 제거
			}
		}
		throw new RuntimeException("Failed to get access token");
	}

	@Override
	public String refreshToken(AuthRequest authRequest) {
		ResponseEntity<JwtResponse> response = jwtClient.authToken(authRequest);

		if (response.getStatusCode().is2xxSuccessful()) {
			String refreshToken = response.getHeaders().getFirst("Refresh-Token");
			if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
				return refreshToken.substring(7); // 'Bearer ' 부분을 제거
			}
		}
		throw new RuntimeException("Failed to get refresh token");
	}
}
