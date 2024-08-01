package store.buzzbook.front.common.aop;

import java.io.IOException;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.user.AlreadyLoginException;
import store.buzzbook.front.common.util.CookieUtils;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OnlyGuestAop {
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final CookieUtils cookieUtils;

	@Before("@annotation(store.buzzbook.front.common.annotation.GuestOnly)")
	public void guestOnly(JoinPoint joinPoint) throws IOException {
		Optional<Cookie> tokenCookie = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);

		if (tokenCookie.isPresent()) {
			log.debug("이미 로그인되어 서비스를 이용할 수 없습니다.");
			throw new AlreadyLoginException();
		}
	}
}
