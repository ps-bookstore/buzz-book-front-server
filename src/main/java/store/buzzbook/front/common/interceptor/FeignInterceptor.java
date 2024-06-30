package store.buzzbook.front.common.interceptor;

import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		// JWT를 헤더에 추가하는 로직 구현
		// 예시로 SecurityContext에서 JWT 토큰을 가져오는 코드
		String jwtToken = getJwtTokenFromSecurityContext();
		if (jwtToken != null) {
			template.header("Authorization", "Bearer " + jwtToken);
		}
	}

	private String getJwtTokenFromSecurityContext() {
		// SecurityContext에서 JWT 토큰을 가져오는 로직 구현
		// 예시로 SecurityContextHolder를 통해 JWT 토큰을 가져오는 코드
		return "your-jwt-token"; // 실제 구현 필요
	}
}