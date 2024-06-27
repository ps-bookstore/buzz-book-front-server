package store.buzzbook.front.common.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	//AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;

	private final AuthenticationSuccessHandler successHandler;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
		AuthenticationSuccessHandler successHandler) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.successHandler = successHandler;
	}

	//AuthenticationManager Bean 등록
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

				@Override
				public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

					CorsConfiguration configuration = new CorsConfiguration();

					// 허용할 출처를 설정
					configuration.setAllowedOrigins(Arrays.asList(
						"http://localhost:8080",
						"http://localhost:8081",
						"http://localhost:8082",
						"http://buzz-book.store",
						"https://buzz-book.store",
						"http://localhost:8090",
						"http://localhost:8091",
						"http://localhost:8761",
						"https://api.tosspayments.com/v1/payments"
						));
					// 허용할 HTTP 메서드 설정 (모든 메서드를 허용)
					configuration.setAllowedMethods(Collections.singletonList("*"));
					// 자격 증명(쿠키, 인증 헤더 등) 허용 설정
					configuration.setAllowCredentials(true);
					// 허용할 헤더 설정 (모든 헤더를 허용)
					configuration.setAllowedHeaders(Collections.singletonList("*"));
					configuration.setMaxAge(3600L);

					configuration.setExposedHeaders(Collections.singletonList("Authorization"));

					return configuration;
				}
			})));

		// http basic 인증방식 disable
		http.httpBasic(AbstractHttpConfigurer::disable);

		http.formLogin(formLogin ->
			formLogin.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("loginId")
				.passwordParameter("password")
				.successHandler(successHandler)
				.permitAll()
		);

		http
			.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/static/**", "/").permitAll() // 정적 자원에 대한 접근 허용
				// .requestMatchers("/admin/**").hasRole("ADMIN") // /admin/** 경로는 ADMIN 권한 필요
				.anyRequest().permitAll()); // todo 그 외 모든 요청은 인증 필요 예정

		// 세션 설정 (세션이 아닌 jwt 토큰을 사용할거기 때문에 STATELESS 설정 필수)
		http.sessionManagement(session -> session
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
