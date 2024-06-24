package store.buzzbook.front.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

// maven dependency 0.12.3 version
@Component
public class JWTUtil {
	private final SecretKey secretKey;
	private final SecretKey refreshTokenKey;

	public JWTUtil(@Value("${spring.jwt.secret}") String secret, @Value("${spring.jwt.refresh}") String refreshSecret) {
		this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
		this.refreshTokenKey = new SecretKeySpec(refreshSecret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	public String getUsername(String token) {

		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token) //claim 확인
			.getPayload() // 데이터 가져오기
			.get("username", String.class);
	}

	public String getRole(String token) {

		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("role", String.class);
	}

	// 토큰이 만료된 토큰인지 아닌지 확인하는 메서드
	public boolean isExpired(String token) {

		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

	// access token 생성 메서드
	public String createJwt(String username, String role, Long expiredMs) {

		return Jwts.builder()
			.claim("username", username) // username
			.claim("role", role) // role 키 추가
			.issuedAt(new Date(System.currentTimeMillis())) // 현재 발행시간
			.expiration(new Date(System.currentTimeMillis() + expiredMs)) // 언제 소멸될건지
			.signWith(secretKey) // 최종 암호화
			.compact();
	}

	// refresh token 생성
	public String createRefreshToken(String username, Long expiredMs) {
		return Jwts.builder()
			.claim("username", username)
			.issuedAt(new Date(System.currentTimeMillis())) // 현재 발행시간
			.expiration(new Date(System.currentTimeMillis() + expiredMs)) // 언제 소멸될건지
			.signWith(refreshTokenKey)
			.compact();
	}

	// Refresh Token에서 username 추출
	public String getUsernameFromRefreshToken(String token) {
		return Jwts.parser()
				.verifyWith(refreshTokenKey)
				.build()
				.parseSignedClaims(token) //claim 확인
				.getPayload() // 데이터 가져오기
				.get("username", String.class);
	}

	// Refresh Token이 만료되었는지 확인
	public boolean isRefreshTokenExpired(String token) {
		return Jwts.parser()
			.verifyWith(refreshTokenKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

}

