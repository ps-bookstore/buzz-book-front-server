package store.buzzbook.front.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import store.buzzbook.front.common.interceptor.FeignInterceptor;

@Configuration
public class FeignConfig {

	@Bean
	public FeignInterceptor feignInterceptor() {
		return new FeignInterceptor();
	}

}
