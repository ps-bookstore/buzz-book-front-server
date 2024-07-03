package store.buzzbook.front.service.jwt.impl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.jwt.JwtClient;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.jwt.AuthRequest;
import store.buzzbook.front.dto.jwt.JwtResponse;
import store.buzzbook.front.service.jwt.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
	private final JwtClient jwtClient;
	private final CookieUtils cookieUtils;

	@Override
	public Map<String, Object> getInfoMapFromJwt(HttpServletRequest request) {
		Optional<Cookie> jwtCookie =  cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refreshCookie =  cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);
		String accessToken = jwtCookie.map(Cookie::getValue).orElse(null);
		String refreshToken = refreshCookie.map(Cookie::getValue).orElse(null);
		accessToken = wrapToken(accessToken);
		refreshToken = wrapToken(refreshToken);

		ResponseEntity<Map<String, Object>> responseEntity = jwtClient.getUserInfo(accessToken, refreshToken);

		if (Objects.isNull(responseEntity.getBody())) {
			log.debug("토큰 인증에 실패 했습니다. : null point exception");
			throw new AuthorizeFailException("Invalid access token");
		}

		if (responseEntity.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
			throw new AuthorizeFailException((String)responseEntity.getBody().get(ERROR),
				(String)responseEntity.getBody().get(MESSAGE));
		}

		return responseEntity.getBody();
	}

	@Override
	public Long getUserIdFromJwt(HttpServletRequest request) {
		Map<String, Object> claims = getInfoMapFromJwt(request);
		return (Long)claims.get(USER_ID);
	}

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


	private String wrapToken(String token) {
		return String.format("Bearer %s", token);
	}
}
