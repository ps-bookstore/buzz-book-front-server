package store.buzzbook.front.service.user;

import org.springframework.security.core.userdetails.UserDetails;

import store.buzzbook.front.dto.user.OauthRegisterRequest;

public interface UserAuthService {
	String PROVIDER_PAYCO = "payco";
	String PAYCO_USER_INFO = "paycoUserInfo";
	String PAYCO_LOGIN_STATUS = "loginStatus";

	// String paycoAuth();

	// PaycoAuthResponse refresh(String refreshToken);

	// void wrapCookie(HttpServletResponse response, PaycoAuthResponse paycoAuthResponse);

	boolean isRegisteredWithOauth(String provideId, String provider);

	// PaycoAuthResponse requestPaycoToken(String code);

	// PaycoUserInfo getPaycoUserInfo(String accessToken, String refreshToken, HttpServletResponse response);

	UserDetails loadUserByProvideIdAndProvider(String provideId, String provider);

	void register(OauthRegisterRequest registerRequest);

	// void logout(String accessToken);

}
