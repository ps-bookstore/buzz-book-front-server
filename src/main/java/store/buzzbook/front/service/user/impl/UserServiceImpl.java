package store.buzzbook.front.service.user.impl;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.AuthenticationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.user.UserClient;
import store.buzzbook.front.common.exception.user.DeactivatedUserException;
import store.buzzbook.front.common.exception.user.PasswordIncorrectException;
import store.buzzbook.front.common.exception.user.PasswordNotConfirmedException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.front.common.exception.user.UserNotFoundException;
import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserApiRequest;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.user.UserService;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserClient userClient;
	private final PasswordEncoder passwordEncoder;


	@Override
	public void registerUser(RegisterUserRequest registerUserRequest) {
		if(!registerUserRequest.confirmedPassword().equals(registerUserRequest.password())){
			log.warn("회원가입 요청 비밀번호와 비밀번호 확인이 다릅니다. : {}, {}", registerUserRequest.password(), registerUserRequest.confirmedPassword());
			throw new PasswordNotConfirmedException();
		}

		RegisterUserApiRequest registerUserApiRequest = createRegisterUserApiRequest(registerUserRequest);
		ResponseEntity<RegisterUserResponse> registerUserResponse = userClient.registerUser(registerUserApiRequest);

		if (registerUserResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
			throw new UserAlreadyExistsException(registerUserApiRequest.loginId());
		}
	}

	@Override
	public LoginUserResponse requestLogin(String loginId) {
		ResponseEntity<LoginUserResponse> loginUserResponse = userClient.requestLogin(loginId);

		if(loginUserResponse.getStatusCode().equals(HttpStatus.FORBIDDEN)){
			throw new DeactivatedUserException(loginId);
		}
		return loginUserResponse.getBody();
	}

	@Override
	public UserInfo successLogin(String loginId) {
		ResponseEntity<UserInfo> userInfo = userClient.successLogin(loginId);

		if(userInfo.getStatusCode().isError()){
			throw new UnknownApiException("로그인 성공 처리 중 오류 : 알 수 없는 오류");
		}

		return userInfo.getBody();
	}

	@Override
	public UserInfo getUserInfo(Long userId) {
		ResponseEntity<UserInfo> userInfo = userClient.getUserInfo();

		if(userInfo.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
			log.debug("회원 조회 실패 : {}", userId);
			throw new UserNotFoundException(String.valueOf(userId));
		}

		return userInfo.getBody();
	}

	@Override
	public void deactivate(Long userId,DeactivateUserRequest deactivateUserRequest) {
		ResponseEntity<Void> responseEntity = userClient.deactivateUser(deactivateUserRequest);

		if(responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
			log.debug("탈퇴 중 잘못된 비밀번호를 입력하였습니다.");
			throw new PasswordIncorrectException();
		}

	}

	@Override
	public UserInfo updateUserInfo(Long userId,UpdateUserRequest updateUserRequest) {
		ResponseEntity<UserInfo> userInfo =userClient.updateUser(updateUserRequest);
		if(userInfo.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
			throw new UserNotFoundException(userId);
		}

		return userInfo.getBody();
	}

	@Override
	public void changePassword(Long userId,ChangePasswordRequest changePasswordRequest) {
		if(!changePasswordRequest.isConfirmed()){
			log.debug("비밀번호 변경 실패 : 새로운 비밀번호 확인 실패 혹은 이전과 같은 비밀번호로 변경");
			throw new PasswordNotConfirmedException("이전 비밀번호와 같거나 새 비밀번호 확인이 틀렸습니다.");
		}

		changePasswordRequest.encryptPassword(passwordEncoder);
		ResponseEntity<Void> responseEntity = userClient.changePassword(changePasswordRequest);

		if(responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
			log.debug("비밀번호 변경 실패 : 이전 비밀번호를 틀렸습니다.");
			throw new PasswordIncorrectException();
		}
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
