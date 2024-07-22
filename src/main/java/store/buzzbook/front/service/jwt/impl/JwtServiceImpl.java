package store.buzzbook.front.service.jwt.impl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.jwt.JwtClient;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.user.ActivateFailException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.exception.user.UserTokenException;
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
	public Map<String, Object> getInfoMapFromJwt(HttpServletRequest request, HttpServletResponse response) {
		Optional<Cookie> jwtCookie =  cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refreshCookie =  cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		String accessToken = jwtCookie.map(Cookie::getValue).orElse(null);
		String refreshToken = refreshCookie.map(Cookie::getValue).orElse(null);

		if(Objects.nonNull(accessToken)){
			accessToken = wrapToken(accessToken);
		}
		if(Objects.nonNull(refreshToken)){
			refreshToken = wrapToken(refreshToken);
		}

		try {
			ResponseEntity<Map<String, Object>> responseEntity = jwtClient.getUserInfo(accessToken, refreshToken);

			if (Objects.isNull(responseEntity.getBody())) {
				log.debug("토큰 인증에 실패 했습니다. : null point exception");
				throw new AuthorizeFailException("Invalid access token");
			}

			HttpHeaders headers = responseEntity.getHeaders();
			String token = headers.getFirst(TOKEN_HEADER);
			String refresh = headers.getFirst(REFRESH_HEADER);

			if(Objects.nonNull(token)){
				Cookie newJwtCookie = cookieUtils.wrapJwtTokenCookie(token);
				response.addCookie(newJwtCookie);
			}

			if(Objects.nonNull(refresh)){
				Cookie newRefreshCookie = cookieUtils.wrapRefreshTokenCookie(refresh);
				response.addCookie(newRefreshCookie);
			}

			return responseEntity.getBody();
		}catch (FeignException.Unauthorized e) {
			throw new AuthorizeFailException("Invalid access token or refresh token");
		}

	}

	@Override
	public Long getUserIdFromJwt(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> claims = getInfoMapFromJwt(request, response);
		return ((Integer)claims.get(USER_ID)).longValue();
	}

	@Override
	public String accessToken(AuthRequest authRequest) {
		ResponseEntity<JwtResponse> response = jwtClient.authToken(authRequest);

		String accessToken = response.getHeaders().getFirst(JwtService.TOKEN_HEADER);
		if (accessToken != null && accessToken.startsWith("Bearer ")) {
			return accessToken.substring(7); // 'Bearer ' 부분을 제거
		}

		throw new UnknownApiException("Failed to get access token");
	}

	@Override
	public String refreshToken(AuthRequest authRequest) {
		ResponseEntity<JwtResponse> response = jwtClient.authToken(authRequest);

		String refreshToken = response.getHeaders().getFirst(JwtService.REFRESH_HEADER);
		if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
			return refreshToken.substring(7); // 'Bearer ' 부분을 제거
		}

		throw new UnknownApiException("Failed to get refresh token");
	}

	@Override
	public String getDormantToken(String loginId) {
		ResponseEntity<String> dormantToken = jwtClient.getDormantToken(loginId);
		return dormantToken.getBody();
	}

	@Override
	public String checkDormantTokenAndCode(String token, String code) {
		try {
			ResponseEntity<String> responseEntity = jwtClient.checkDormantToken(token,code);
			return responseEntity.getBody();
		}catch (FeignException.NotFound e) {
			log.debug("잘못된 활성화 토큰 혹은 코드입니다.");
			throw new ActivateFailException();
		}
	}

	@Override
	public void existsDormantToken(String token) {
		try {
			jwtClient.existDormantToken(token);
		}catch (FeignException.NotFound e) {
			log.debug("잘못된 활성화 토큰입니다.");
			throw new ActivateFailException();
		}
	}

	private String wrapToken(String token) {
		return String.format("Bearer %s", token);
	}

}
