package store.buzzbook.front.common.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.dto.jwt.AuthRequest;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.UserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	private final UserService userService;
	private final JwtService jwtService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		UserInfo userInfo = null;
		try {
			userInfo = userService.successLogin(authentication.getName());
			request.getSession().setAttribute("user", userInfo);
			log.info("login success");

			// JWT 발급 요청
			AuthRequest authRequest = new AuthRequest(userInfo.getLoginId(), userInfo.isAdmin() ? "ADMIN" : "USER");

			String accessToken = jwtService.accessToken(authRequest);
			String refreshToken = jwtService.refreshToken(authRequest);

			if (accessToken != null && refreshToken != null) {
				// Access Token 쿠키 설정
				Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
				accessTokenCookie.setPath("/");
				accessTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효 기간 설정 (예: 7일)

				// Refresh Token 쿠키 설정
				Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
				refreshTokenCookie.setPath("/");
				refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효 기간 설정 (예: 7일)

				// 응답에 쿠키 추가
				response.addCookie(accessTokenCookie);
				response.addCookie(refreshTokenCookie);
			} else {
				log.error("Failed to get tokens");
			}

			response.sendRedirect(request.getContextPath() + "/home");

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response.sendRedirect(request.getContextPath() + "/login");
		}
	}
}
