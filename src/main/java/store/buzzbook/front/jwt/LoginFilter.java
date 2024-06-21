package store.buzzbook.front.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.dto.user.CustomUserDetails;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	// JWTUtil 주입
	private final JWTUtil jwtUtil;

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter("loginId");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws
		AuthenticationException {
		// 클라이언트 요청에서 username, password추출
		String username = obtainUsername(req);
		String password = obtainPassword(req);

		log.info("username {}", username);

		// 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,
			null);

		log.info("authToken {}", authToken);

		// authenticationManager에 던져주면 자동으로 검증해줌
		return authenticationManager.authenticate(authToken);
	}

	//로그인 성공시 실행하는 메소드 (여기서 JWT 를 발급)
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException {
		// 특정 유저 확인
		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();

		String role = auth.getAuthority();

		String token = jwtUtil.createJwt(username, role, 60 * 60 * 10000L);

		// HTTP 인증 방식은 RFC 7235 정의에 따라 아래 인증 헤더 형태를 가져야 한다.
		// Authorization: 타입 인증토큰

		// 예시
		// Authorization: Bearer 인증토큰(string)
		response.addHeader("Authorization", "Bearer " + token);

		log.info("success token {}", response.getHeader("Authorization"));

		// JSON 형태로 응답 본문 작성
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"message\": \"성공\", \"status\": 200, \"id\": \"" + username + "\"}");
		response.getWriter().flush();
	}

	//로그인 실패시 실행하는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		log.info("fail");
	}
}

