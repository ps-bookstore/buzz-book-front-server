package store.buzzbook.front.service.user.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.user.UserClient;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.user.ActivateFailException;
import store.buzzbook.front.common.exception.user.AddressMaxCountException;
import store.buzzbook.front.common.exception.user.DeactivatedUserException;
import store.buzzbook.front.common.exception.user.DormantUserException;
import store.buzzbook.front.common.exception.user.PasswordIncorrectException;
import store.buzzbook.front.common.exception.user.PasswordNotConfirmedException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.front.common.exception.user.UserNotFoundException;
import store.buzzbook.front.dto.coupon.CouponResponse;
import store.buzzbook.front.dto.point.PointLogResponse;
import store.buzzbook.front.dto.user.AddressInfoResponse;
import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.CreateAddressRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.DoorayMessagePayload;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserApiRequest;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.dto.user.UpdateAddressRequest;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.UserService;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserClient userClient;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void registerUser(RegisterUserRequest registerUserRequest) {
		if (!registerUserRequest.confirmedPassword().equals(registerUserRequest.password())) {
			log.debug("회원가입 요청 비밀번호와 비밀번호 확인이 다릅니다. : {}, {}", registerUserRequest.password(),
				registerUserRequest.confirmedPassword());
			throw new PasswordNotConfirmedException();
		}

		try {
			RegisterUserApiRequest registerUserApiRequest = registerUserRequest.toApiRequest(passwordEncoder);
			userClient.registerUser(registerUserApiRequest);
		}catch (FeignException e) {
			if(e.status()==HttpStatus.BAD_REQUEST.value()){
				log.debug("이미 존재하는 login id입니다.");
				throw new UserAlreadyExistsException(registerUserRequest.loginId());
			}
			throw e;
		}
	}

	@Override
	public LoginUserResponse requestLogin(String loginId) {
		ResponseEntity<LoginUserResponse> loginUserResponse = null;
		try {
			loginUserResponse = userClient.requestLogin(loginId);
		}catch (FeignException e){
			if(e.status()==HttpStatus.FORBIDDEN.value()){
				throw new DeactivatedUserException(loginId);
			}
			throw e;
		}
		return loginUserResponse.getBody();
	}

	@Override
	public UserInfo successLogin(String loginId) {
		ResponseEntity<UserInfo> userInfo = null;

		try {
			userInfo = userClient.successLogin(loginId);

			if (userInfo.getStatusCode().isError()) {
				throw new UnknownApiException("로그인 성공 처리 중 오류 : 알 수 없는 오류");
			}
		}catch (FeignException e){
			if(e.status() == HttpStatus.NOT_ACCEPTABLE.value()) {
				log.debug("휴면된 유저의 로그인 요청입니다.");
				String dormantToken = jwtService.getDormantToken(loginId);
				throw new DormantUserException(dormantToken);
			}
			throw e;
		}

		return userInfo.getBody();
	}

	@Override
	public UserInfo getUserInfo(Long userId) {
		ResponseEntity<UserInfo> userInfo = null;

		try {
			userInfo =  userClient.getUserInfo();
		}catch (FeignException e){
			if(e.status() == HttpStatus.BAD_REQUEST.value()) {
				log.debug("회원 조회 실패 : {}", userId);
				throw new UserNotFoundException(userId);
			}
			throw e;
		}

		return userInfo.getBody();
	}

	@Override
	public void deactivate(Long userId, DeactivateUserRequest deactivateUserRequest) {
		try {
			userClient.deactivateUser(deactivateUserRequest);
		} catch (FeignException e){
			if(e.status() == HttpStatus.BAD_REQUEST.value()) {
				log.debug("탈퇴 중 잘못된 비밀번호를 입력하였습니다.");
				throw new PasswordIncorrectException();
			}
			throw e;
		}
	}

	@Override
	public void updateUserInfo(Long userId, UpdateUserRequest updateUserRequest) {
		try {
			userClient.updateUser(updateUserRequest);
		} catch (FeignException e){
			if(e.status() == HttpStatus.BAD_REQUEST.value()) {
				log.debug("회원 정보 수정 중 회원 조회 실패");
				throw new UserNotFoundException(userId);
			}
			throw e;
		}

	}

	@Override
	public void changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
		if (!changePasswordRequest.isConfirmed()) {
			log.debug("비밀번호 변경 실패 : 새로운 비밀번호 확인 실패 혹은 이전과 같은 비밀번호로 변경");
			throw new PasswordNotConfirmedException("이전 비밀번호와 같거나 새 비밀번호 확인이 틀렸습니다.");
		}

		changePasswordRequest.encryptPassword(passwordEncoder);
		try {
			userClient.changePassword(changePasswordRequest);
		}catch (FeignException e){
			if(e.status() == HttpStatus.BAD_REQUEST.value()) {
				log.debug("비밀번호 변경 실패 : 이전 비밀번호를 틀렸습니다.");
				throw new PasswordIncorrectException();
			}
			throw e;
		}
	}

	@Override
	public List<CouponResponse> getUserCoupons(String couponStatusName) {
		try {
			return userClient.getUserCoupons(couponStatusName);
		} catch (FeignException e) {
			if (e.status() == 404) {
				return Collections.emptyList();
			} else {
				throw e;
			}
		}
	}

	@Override
	public Page<PointLogResponse> getUserPoints(Pageable pageable) {
		return userClient.getPointLogs(pageable);
	}

	@Override
	public List<AddressInfoResponse> getAddressList() {
		ResponseEntity<List<AddressInfoResponse>> responseEntity = null;

		try {
			responseEntity = userClient.getAddressList();
		}catch (FeignException e){
			if(e.status() == HttpStatus.BAD_REQUEST.value()) {
				log.debug("주소 조회 중 잘못된 유저의 요청이 발견됐습니다.");
				throw new UserNotFoundException();
			} else if (e.status() == HttpStatus.UNAUTHORIZED.value()) {
				log.debug("주소 조회 중 잘못된 유저 토큰의 요청이 발견됐습니다.");
				throw new AuthorizeFailException("주소 조회 중 잘못된 유저 토큰의 요청이 발견됐습니다.");
			}
			throw e;
		}

		return responseEntity.getBody();
	}

	@Override
	public void updateAddress(UpdateAddressRequest updateAddressRequest) {
		try {
			userClient.updateAddress(updateAddressRequest);
		}catch (FeignException e){
			if(e.status() == HttpStatus.BAD_REQUEST.value()) {
				log.debug("주소 수정 중 잘못된 유저의 요청이 발견됐습니다.");
				throw new UserNotFoundException();
			} else if (e.status() == HttpStatus.UNAUTHORIZED.value()) {
				log.debug("주소 수정 중 잘못된 유저 토큰의 요청이 발견됐습니다.");
				throw new AuthorizeFailException("주소 수정 중 잘못된 유저 토큰의 요청이 발견됐습니다.");
			}
			throw e;
		}

	}

	@Override
	public void deleteAddress(Long addressId) {
		try {
			userClient.deleteAddress(addressId);
		}catch (FeignException e){
			if(e.status() == HttpStatus.BAD_REQUEST.value()) {
				log.debug("주소 삭제 중 잘못된 유저의 요청이 발견됐습니다.");
				throw new UserNotFoundException();
			} else if (e.status() == HttpStatus.UNAUTHORIZED.value()) {
				log.debug("주소 삭제 중 잘못된 유저 토큰의 요청이 발견됐습니다.");
				throw new AuthorizeFailException("주소 삭제 중 잘못된 유저 토큰의 요청이 발견됐습니다.");
			}
			throw e;
		}
	}

	@Override
	public void createAddress(CreateAddressRequest createAddressRequest) {
		try {
			userClient.createAddress(createAddressRequest);
		}catch (FeignException e){
			if(e.status() == HttpStatus.BAD_REQUEST.value()) {
				log.debug("주소 생성 중 잘못된 유저의 요청이 발견됐습니다.");
				throw new UserNotFoundException();
			} else if (e.status() == HttpStatus.UNAUTHORIZED.value()) {
				log.debug("주소 생성 중 잘못된 유저 토큰의 요청이 발견됐습니다.");
				throw new AuthorizeFailException("주소 생성 중 잘못된 유저 토큰의 요청이 발견됐습니다.");
			} else if (e.status() == HttpStatus.NOT_ACCEPTABLE.value()) {
				log.debug("주소 최대 저장 갯수를 초과했습니다. 10");
				throw new AddressMaxCountException();
			}
			throw e;
		}
	}

	@Override
	public void activate(String loginId) {
		try {
			userClient.activateUser(loginId);
		}catch (FeignException e){
			if(e.status() == HttpStatus.BAD_REQUEST.value()) {
				log.debug("잘못된 아이디 또는 이미 활성화된 아이디로 활성화 요청을 했습니다.");
				throw new ActivateFailException("잘못된 아이디 또는 이미 활성화된 아이디로 활성화 요청을 했습니다.");
			} else if (e.status() == HttpStatus.FORBIDDEN.value()) {
				log.debug("탈퇴한 아이디로 활성화 요청을 했습니다.");
				throw new DeactivatedUserException(loginId);
			}
			throw e;
		}
	}
}
