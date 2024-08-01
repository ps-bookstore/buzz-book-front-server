package store.buzzbook.front.service.user;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.dto.user.OauthRegisterRequest;
import store.buzzbook.front.dto.user.PaycoAuthResponse;
import store.buzzbook.front.dto.user.PaycoUserInfo;

public interface UserAuthService {
	String PROVIDER_PAYCO = "payco";
	String PAYCO_USER_INFO = "paycoUserInfo";
	String PAYCO_LOGIN_STATUS = "loginStatus";

	String paycoAuth();

	PaycoAuthResponse refresh(String refreshToken);

	void wrapCookie(HttpServletResponse response, PaycoAuthResponse paycoAuthResponse);

	boolean isRegisteredWithOauth(String provideId, String provider);

	PaycoAuthResponse requestPaycoToken(String code);

	PaycoUserInfo getPaycoUserInfo(String accessToken, String refreshToken, HttpServletResponse response);

	UserDetails loadUserByProvideIdAndProvider(String provideId, String provider);

	void register(OauthRegisterRequest registerRequest);

	void logout(String accessToken);

}
