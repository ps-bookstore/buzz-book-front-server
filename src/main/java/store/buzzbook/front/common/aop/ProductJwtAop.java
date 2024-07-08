package store.buzzbook.front.common.aop;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.service.jwt.JwtService;

@Component
@RequiredArgsConstructor
@Aspect
@Slf4j
public class ProductJwtAop {
	private final JwtService jwtService;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final CookieUtils cookieUtils;

	//Product AdminPage jwt 토큰
	@Before("@annotation(store.buzzbook.front.common.annotation.ProductJwtValidate)")
	public void authenticate() throws Throwable {
		Optional<Cookie> authorization = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		if (authorization.isEmpty() && refresh.isEmpty()) {
			throw new AuthorizeFailException("로그인을 해주세요");
		}

		Map<String, Object> claims = jwtService.getInfoMapFromJwt(request, response);

		Long userId = claims.get(JwtService.USER_ID) instanceof Integer ? ((Integer) claims.get(JwtService.USER_ID)).longValue() : (Long) claims.get(JwtService.USER_ID);
		String loginId = (String) claims.get(JwtService.LOGIN_ID);
		String role = (String) claims.get(JwtService.ROLE);

		if (Objects.isNull(userId) || Objects.isNull(loginId) || Objects.isNull(role)) {
			throw new AuthorizeFailException("사용자 정보가 null 입니다.");
		}

		if (!"ADMIN".equals(role)) {
			log.info("{}",role);
			throw new AuthorizeFailException("접근 권한이 없습니다.");
		}

		request.setAttribute(JwtService.USER_ID, userId);
		request.setAttribute(JwtService.LOGIN_ID, loginId);
		request.setAttribute(JwtService.ROLE, role);
	}
}
