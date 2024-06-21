package store.buzzbook.front.service.user.impl;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.user.UserRestClient;
import store.buzzbook.front.common.exception.user.UnknownUserException;
import store.buzzbook.front.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.front.common.exception.user.UserNotFoundException;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserApiRequest;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.user.UserService;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserRestClient userRestClient;

	private final PasswordEncoder passwordEncoder;


	public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {

		if(!registerUserRequest.confirmedPassword().equals(registerUserRequest.password())){
			log.error("Passwords do not match");
			return null;
		}

		RegisterUserApiRequest registerUserApiRequest = createRegisterUserApiRequest(registerUserRequest);


		RegisterUserResponse registerUserResponse = null;
		try{
			registerUserResponse = userRestClient.registerUser(registerUserApiRequest);
		}catch (UserAlreadyExistsException e){
			log.warn(e.getMessage());
		}


		return registerUserResponse;
	}

	@Override
	public LoginUserResponse requestLogin(String loginId) {
		return userRestClient.requestLogin(loginId);
	}

	@Override
	public UserInfo successLogin(String loginId) {
		UserInfo userInfo = userRestClient.successLogin(loginId);
		if(Objects.isNull(userInfo)){
			throw new UnknownUserException("로그인 성공 처리 중 오류 : 알 수 없는 오류");
		}

		return userRestClient.successLogin(loginId);
	}

	private RegisterUserApiRequest createRegisterUserApiRequest(RegisterUserRequest registerUserRequest) {
		return RegisterUserApiRequest.builder()
			.contactNumber(registerUserRequest.contactNumber())
			.email(registerUserRequest.email())
			.name(registerUserRequest.name())
			.birthday(registerUserRequest.birthday())
			.loginId(registerUserRequest.loginId())
			.password(passwordEncoder.encode(registerUserRequest.password()))
			.build();
	}

}
