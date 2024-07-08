package store.buzzbook.front.common.aop;

import java.util.Map;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.service.jwt.JwtService;

@Aspect
@RequiredArgsConstructor
@Component
public class OrderJwtAop {
	private final JwtService jwtService;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	@Before("@annotation(store.buzzbook.front.common.annotation.OrderJwtValidate)")
	public void authenticated(JoinPoint joinPoint) throws Throwable {
		String authorizationHeader = request.getHeader("Authorization");

		if (Objects.nonNull(authorizationHeader)) {
			Map<String, Object> claims = jwtService.getInfoMapFromJwt(request, response);

			Long userId = (Long)claims.get(JwtService.USER_ID);
			String loginId = (String)claims.get(JwtService.LOGIN_ID);
			String role = (String)claims.get(JwtService.ROLE);

			if (Objects.isNull(userId) || Objects.isNull(loginId) || Objects.isNull(role)) {
				throw new AuthorizeFailException("user info가 null입니다.");
			}

			request.setAttribute(JwtService.USER_ID, userId);
			request.setAttribute(JwtService.LOGIN_ID, loginId);
			request.setAttribute(JwtService.ROLE, role);
		}
	}
}
