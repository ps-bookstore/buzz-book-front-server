package store.buzzbook.front.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.jwt.JWTUtil;

@Component
public class JwtInterceptor implements HandlerInterceptor {

	private final JWTUtil jwtUtil;

	public JwtInterceptor(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return unauthorized(response, "Token not found or invalid");
		}

		Cookie jwtCookie = getCookie(cookies, "jwtToken");
		if (jwtCookie == null) {
			return unauthorized(response, "Token not found or invalid");
		}

		String token = jwtCookie.getValue();
		if (!jwtUtil.isExpired(token)) {
			return true;
		}

		Cookie refreshTokenCookie = getCookie(cookies, "refreshToken");
		if (refreshTokenCookie == null) {
			return unauthorized(response, "Refresh token not found");
		}

		String refreshToken = refreshTokenCookie.getValue();
		if (jwtUtil.isRefreshTokenExpired(refreshToken)) {
			return unauthorized(response, "Refresh token expired");
		}

		String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
		String newAccessToken = jwtUtil.createJwt(username, "USER_ROLE", 60 * 60 * 1000L); // 1시간 유효 토큰 생성

		Cookie newTokenCookie = new Cookie("jwtToken", newAccessToken);
		newTokenCookie.setHttpOnly(true);
		newTokenCookie.setSecure(true); // HTTPS에서만 사용
		newTokenCookie.setPath("/");
		newTokenCookie.setMaxAge(60 * 60); // 1시간

		response.addCookie(newTokenCookie);
		return true;
	}

	private Cookie getCookie(Cookie[] cookies, String name) {
		for (Cookie cookie : cookies) {
			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	private boolean unauthorized(HttpServletResponse response, String message) throws Exception {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(message);
		return false;
	}
}
