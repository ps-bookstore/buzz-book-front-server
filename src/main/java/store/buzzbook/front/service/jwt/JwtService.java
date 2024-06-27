package store.buzzbook.front.service.jwt;

import store.buzzbook.front.dto.jwt.AuthRequest;

public interface JwtService {
	String accessToken(AuthRequest authRequest);
	String refreshToken(AuthRequest authRequest);
}
