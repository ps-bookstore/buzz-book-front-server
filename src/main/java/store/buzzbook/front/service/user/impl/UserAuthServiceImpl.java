package store.buzzbook.front.service.user.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.user.PaycoClient;
import store.buzzbook.front.client.user.PaycoInfoClient;
import store.buzzbook.front.client.user.UserAuthClient;
import store.buzzbook.front.common.config.PaycoOauthProperties;
import store.buzzbook.front.common.config.PaycoOauthSecrets;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.user.CustomUserDetails;
import store.buzzbook.front.dto.user.JwtLoginUser;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.OauthRegisterRequest;
import store.buzzbook.front.dto.user.PaycoAuthResponse;
import store.buzzbook.front.dto.user.PaycoLogoutResponse;
import store.buzzbook.front.dto.user.PaycoUserInfo;
import store.buzzbook.front.service.user.UserAuthService;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
	private final CookieUtils cookieUtils;
	private final PaycoOauthProperties paycoOauthProperties;
	private final PaycoOauthSecrets paycoOauthSecrets;
	private final UserAuthClient userAuthClient;
	private final PasswordEncoder passwordEncoder;


	private PaycoInfoClient paycoInfoClient;
	private PaycoClient paycoClient;

	@Autowired
	@Lazy
	public void setPaycoClient(PaycoClient paycoClient, PaycoInfoClient paycoInfoClient) {
		this.paycoClient = paycoClient;
		this.paycoInfoClient = paycoInfoClient;
	}

	@Override
	public String paycoAuth(){
		StringBuilder authRequest = new StringBuilder(paycoOauthProperties.getAuthorizationUri());
		authRequest.append("?response_type=").append(paycoOauthProperties.getCodeGrantType());
		authRequest.append("&client_id=").append(paycoOauthSecrets.getClientId());
		authRequest.append("&redirect_uri=").append(paycoOauthProperties.getRedirectUri());
		authRequest.append("&serviceProviderCode=").append("FRIENDS");
		authRequest.append("&userLocale=").append("ko_KR");

		return authRequest.toString();
	}

	@Override
	public void wrapCookie(HttpServletResponse response, PaycoAuthResponse paycoAuthResponse) {
		Cookie paycoAccessCookie = cookieUtils.wrapCookie(CookieUtils.COOKIE_PAYCO_ACCESS_KEY,
			paycoAuthResponse.getAccessToken(),
			Integer.parseInt(paycoAuthResponse.getExpiresIn()));
		Cookie paycoRefreshCookie = cookieUtils.wrapCookie(CookieUtils.COOKIE_PAYCO_REFRESH_KEY,
			paycoAuthResponse.getRefreshToken(),
			Integer.parseInt(paycoAuthResponse.getExpiresIn()));

		response.addCookie(paycoAccessCookie);
		response.addCookie(paycoRefreshCookie);
	}

	@Override
	public boolean isRegisteredWithOauth(String provideId, String provider) {
		ResponseEntity<Boolean> responseEntity = userAuthClient.isRegistered(provideId,provider);

		return Objects.requireNonNull(responseEntity.getBody());
	}

	@Override
	public PaycoAuthResponse requestPaycoToken(String code){
		ResponseEntity<PaycoAuthResponse> result = paycoClient.requestToken(
			paycoOauthProperties.getAuthorizationGrantType(),
			paycoOauthSecrets.getClientId(),
			paycoOauthSecrets.getClientSecret(),
			code);

		return result.getBody();
	}

	@Override
	public PaycoAuthResponse refresh(String refreshToken){
		ResponseEntity<PaycoAuthResponse> result = paycoClient.refreshToken(
			paycoOauthProperties.getRefreshGrantType(),
			paycoOauthSecrets.getClientId(),
			paycoOauthSecrets.getClientSecret(),
			refreshToken
		);

		PaycoAuthResponse paycoAuthResponse = result.getBody();

		if (Objects.isNull(paycoAuthResponse)){
			throw new AuthorizeFailException("Invalid payco refresh token");
		}

		return result.getBody();
	}

	@Override
	public PaycoUserInfo getPaycoUserInfo(String accessToken, String refreshToken, HttpServletResponse response) {
		if(accessToken == null){
			PaycoAuthResponse paycoAuthResponse = refresh(refreshToken);
			wrapCookie(response, paycoAuthResponse);
			accessToken = paycoAuthResponse.getAccessToken();
		}

		ResponseEntity<PaycoUserInfo> responseEntity =
			paycoInfoClient.requestUserInfo(paycoOauthSecrets.getClientId(), accessToken);

		return responseEntity.getBody();
	}

	@Override
	public void register(OauthRegisterRequest registerRequest) {
		try {
			registerRequest.generateLoginIdAndPassword(passwordEncoder);
			userAuthClient.registerUser(registerRequest);
		}catch (FeignException.BadRequest e){
			log.debug("이미 존재하는 login id입니다.");
			throw new UserAlreadyExistsException(registerRequest.getLoginId());
		}
	}

	@Override
	public void logout(String accessToken) {
		ResponseEntity<PaycoLogoutResponse> responseEntity = paycoClient.logout(
			paycoOauthSecrets.getClientId(),
			accessToken,
			paycoOauthSecrets.getClientSecret()
		);

		if(Objects.requireNonNull(responseEntity.getBody()).getLoginStatus() != 0){
			throw new UnknownApiException("payco 로그아웃 실패!");
		}
	}

	@Override
	public UserDetails loadUserByProvideIdAndProvider(String provideId, String provider) throws UsernameNotFoundException {
		try {
			ResponseEntity<LoginUserResponse> responseEntity = userAuthClient.requestLogin(provideId,provider);
			LoginUserResponse loginUserResponse = responseEntity.getBody();

			if(Objects.isNull(loginUserResponse)) {
				throw new UsernameNotFoundException("User not found");
			}

			String role = loginUserResponse.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";

			JwtLoginUser jwtLoginUser = JwtLoginUser.builder()
				.username(loginUserResponse.loginId())
				.password(loginUserResponse.password())
				.role(role).build();

			return new CustomUserDetails(jwtLoginUser);
		}catch (FeignException.NotFound e){
			throw new UsernameNotFoundException(e.getMessage());
		}
	}

}
