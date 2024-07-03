package store.buzzbook.front.common.aop;

import java.util.Map;
import java.util.Objects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.auth.CouponAuthorizeFailException;
import store.buzzbook.front.service.jwt.JwtService;

@Aspect
@RequiredArgsConstructor
@Component
public class CouponJwtAop {
	private final JwtService jwtService;
	private final HttpServletRequest request;

	@Before("@annotation(store.buzzbook.front.common.annotation.CouponJwtValidate)")
	public void authenticate() {
		String authorizationHeader = request.getHeader("Authorization");

		if (Objects.isNull(authorizationHeader)) {
			throw new CouponAuthorizeFailException("비회원은 쿠폰을 다운로드 할 수 없습니다.");
		}

		Map<String, Object> claims = jwtService.getInfoMapFromJwt(request);

		Long userId = (Long)claims.get(JwtService.USER_ID);

		if (Objects.isNull(userId)) {
			throw new AuthorizeFailException("user info 가 null 입니다.");
		}

		request.setAttribute(JwtService.USER_ID, userId);
	}
}
