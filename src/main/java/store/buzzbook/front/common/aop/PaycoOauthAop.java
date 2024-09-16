// package store.buzzbook.front.common.aop;
//
// import java.util.Map;
// import java.util.Optional;
//
// import org.aspectj.lang.JoinPoint;
// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Before;
// import org.springframework.stereotype.Component;
//
// import jakarta.servlet.http.Cookie;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
// import store.buzzbook.front.common.util.CookieUtils;
// import store.buzzbook.front.dto.user.PaycoUserInfo;
// import store.buzzbook.front.service.user.UserAuthService;
//
// @Aspect
// @RequiredArgsConstructor
// @Component
// public class PaycoOauthAop {
// 	private final HttpServletRequest request;
// 	private final HttpServletResponse response;
// 	private final CookieUtils cookieUtils;
// 	private final UserAuthService userAuthService;
//
// 	@Before("@annotation(store.buzzbook.front.common.annotation.PaycoOauth)")
// 	public void authenticate(JoinPoint joinPoint) throws Throwable {
// 		Optional<Cookie> authorization = cookieUtils.getCookie(request, CookieUtils.COOKIE_PAYCO_ACCESS_KEY);
// 		Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_PAYCO_REFRESH_KEY);
//
//
// 		if (authorization.isEmpty() && refresh.isEmpty()) {
// 			throw new AuthorizeFailException("payco token이 없습니다.");
// 		}
//
// 		String accessToken = authorization.map(Cookie::getValue).orElse(null);
// 		String refreshToken = refresh.map(Cookie::getValue).orElse(null);
//
// 		PaycoUserInfo paycoUserInfo = userAuthService.getPaycoUserInfo(accessToken, refreshToken, response);
// 		request.setAttribute(UserAuthService.PAYCO_USER_INFO, paycoUserInfo);
// 	}
// }
