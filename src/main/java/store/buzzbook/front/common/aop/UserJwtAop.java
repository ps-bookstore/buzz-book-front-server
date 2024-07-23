package store.buzzbook.front.common.aop;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.service.jwt.JwtService;

@Aspect
@RequiredArgsConstructor
@Component
public class UserJwtAop {
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final CookieUtils cookieUtils;

    @Before("@annotation(store.buzzbook.front.common.annotation.JwtValidate)")
    public void authenticate(JoinPoint joinPoint) throws Throwable {
        Optional<Cookie> authorizationHeader = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
        Optional<Cookie> refreshHeader = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);


        //비회원 -- uuid 체크 (존재유무로 위변조체크)
        if (authorizationHeader.isEmpty() && refreshHeader.isEmpty()) {
            throw new AuthorizeFailException("jwt token이 없습니다.");
        }
        //회원
        Map<String, Object> claims = jwtService.getInfoMapFromJwt(request, response);

        Long userId = ((Integer) claims.get(JwtService.USER_ID)).longValue();
        String loginId = (String) claims.get(JwtService.LOGIN_ID);
        String role = (String) claims.get(JwtService.ROLE);

        if (Objects.isNull(userId) || Objects.isNull(loginId) || Objects.isNull(role)) {
            throw new AuthorizeFailException("user info가 null입니다.");
        }

        request.setAttribute(JwtService.USER_ID, userId);
        request.setAttribute(JwtService.LOGIN_ID, loginId);
        request.setAttribute(JwtService.ROLE, role);
    }

}
