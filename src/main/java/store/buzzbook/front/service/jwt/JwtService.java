package store.buzzbook.front.service.jwt;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.front.dto.jwt.AuthRequest;

public interface JwtService {
	String TOKEN_HEADER = "Authorization";
	String REFRESH_HEADER = "Refresh-Token";
	String MESSAGE = "message";
	String ERROR = "error";
	String USER_ID = "userId";
	String ROLE = "role";
	String LOGIN_ID = "loginId";

	Map<String, Object> getInfoMapFromJwt(HttpServletRequest request);
	Long getUserIdFromJwt(HttpServletRequest request);
	String accessToken(AuthRequest authRequest);
	String refreshToken(AuthRequest authRequest);
}
