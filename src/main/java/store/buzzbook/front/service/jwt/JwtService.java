package store.buzzbook.front.service.jwt;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.dto.jwt.AuthRequest;

public interface JwtService {
	String TOKEN_HEADER = "Authorization";
	String REFRESH_HEADER = "Refresh-Token";
	String MESSAGE = "message";
	String ERROR = "error";
	String USER_ID = "userId";
	String ROLE = "role";
	String LOGIN_ID = "loginId";
	String TOKEN_FORMAT = "Bearer %s";

	Map<String, Object> getInfoMapFromJwt(HttpServletRequest request, HttpServletResponse response);
	Long getUserIdFromJwt(HttpServletRequest request, HttpServletResponse response);
	String accessToken(AuthRequest authRequest);
	String refreshToken(AuthRequest authRequest);
	String getDormantToken(String loginId);
	String checkDormantTokenAndCode(String token, String code);
	void existsDormantToken(String token);
}
