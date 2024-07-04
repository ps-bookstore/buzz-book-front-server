package store.buzzbook.front.common.interceptor;

import java.util.Optional;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.util.CookieUtils;

@RequiredArgsConstructor
public class FeignInterceptor implements RequestInterceptor {
	private final CookieUtils cookieUtils;

	@Override
	public void apply(RequestTemplate template) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();


		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();

			Optional<Cookie> jwtCookie = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
			Optional<Cookie> refreshCookie = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

			if(jwtCookie.isPresent()) {
				String jwtToken = jwtCookie.get().getValue();
				template.header(CookieUtils.COOKIE_JWT_ACCESS_KEY, jwtToken);
			}

			if (refreshCookie.isPresent()) {
				String refreshToken = refreshCookie.get().getValue();
				template.header(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);
			}
		}
	}
}