package store.buzzbook.front.common.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.user.DormantUserException;
import store.buzzbook.front.common.util.CookieUtils;
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
	private final CookieUtils cookieUtils;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		UserInfo userInfo = null;
		try {
			userInfo = userService.successLogin(authentication.getName());
			log.debug("login success");

			// JWT 발급 요청
			String role = userInfo.isAdmin() ? "ADMIN" : "USER";
			AuthRequest authRequest = AuthRequest.builder()
				.loginId(userInfo.getLoginId())
				.userId(userInfo.getId())
				.role(role)
				.build();

			String accessToken = jwtService.accessToken(authRequest);
			String refreshToken = jwtService.refreshToken(authRequest);

			if (accessToken != null && refreshToken != null) {
				// Access Token 쿠키 설정
				Cookie accessTokenCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, accessToken);
				accessTokenCookie.setPath("/");
				accessTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효 기간 설정 (예: 7일)

				// Refresh Token 쿠키 설정
				Cookie refreshTokenCookie = new Cookie(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);
				refreshTokenCookie.setPath("/");
				refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효 기간 설정 (예: 7일)

				// 응답에 쿠키 추가
				response.addCookie(accessTokenCookie);
				response.addCookie(refreshTokenCookie);
				cookieUtils.deleteCookie(request,response, CookieUtils.COOKIE_CART_KEY);
				response.sendRedirect(request.getContextPath() + "/home");

			} else {
				log.error("Failed to get tokens");
				response.sendRedirect(request.getContextPath() + "/login");
			}
		}catch (DormantUserException e) {
			String token = e.getDormantToken();
			response.sendRedirect(request.getContextPath() + String.format("/activate?token=%s", token));
		}
		catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/login");
			log.error(e.getMessage(), e);
		}
	}
}
