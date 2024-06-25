package store.buzzbook.front.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.interceptor.JwtInterceptor;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        // 콜솔에 No mapping for GET /favicon.ico 경고 해제하기
        // spring boot 는 자동으로 resources/static/favicon.ico 를 불러오기 때문에 custom 경로를 이용할 시 수동 설정해줘야 함
        registry.addResourceHandler("/favicon.ico")
            .addResourceLocations("classpath:/static/buzz_bee_icon/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/api/**") // 인터셉터를 적용할 경로 설정
            .excludePathPatterns("/api/login", "/api/refresh"); // 인터셉터를 제외할 경로 설정
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

