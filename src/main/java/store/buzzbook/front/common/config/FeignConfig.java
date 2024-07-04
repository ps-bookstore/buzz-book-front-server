package store.buzzbook.front.common.config;

import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.interceptor.FeignInterceptor;
import store.buzzbook.front.common.util.CookieUtils;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {


	public FeignInterceptor feignInterceptor() {
		return new FeignInterceptor(new CookieUtils());
	}

}
