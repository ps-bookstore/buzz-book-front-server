package store.buzzbook.front.common.aop;

import java.util.Map;
import java.util.Optional;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.exception.auth.CouponAuthorizeFailException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.service.jwt.JwtService;

@Aspect
@RequiredArgsConstructor
@Component
public class CouponJwtAop {
	private final JwtService jwtService;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final CookieUtils cookieUtils;

	@Before("@annotation(store.buzzbook.front.common.annotation.CouponJwtValidate)")
	public void authenticate() {
		Optional<Cookie> authorizationOptional = cookieUtils.getCookie(request, "Authorization");
		Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		if (authorizationOptional.isEmpty() && refresh.isEmpty()) {
			throw new CouponAuthorizeFailException("비회원은 쿠폰을 다운로드 할 수 없습니다.");
		}

		Map<String, Object> claims = jwtService.getInfoMapFromJwt(request, response);

		Long userId = ((Integer)claims.get(JwtService.USER_ID)).longValue();

		request.setAttribute(JwtService.USER_ID, userId);

	}
}
