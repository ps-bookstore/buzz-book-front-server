package store.buzzbook.front.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.front.common.util.CookieUtils;

@Component
public class FeignInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			String jwtToken = request.getHeader(CookieUtils.COOKIE_JWT_ACCESS_KEY);
			String refreshToken = request.getHeader(CookieUtils.COOKIE_JWT_REFRESH_KEY);
			if (jwtToken != null && refreshToken != null) {
				template.header(CookieUtils.COOKIE_JWT_ACCESS_KEY, jwtToken);
				template.header(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);
			}
		}
	}
}