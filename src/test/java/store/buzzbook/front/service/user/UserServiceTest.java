package store.buzzbook.front.service.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

import feign.FeignException;
import store.buzzbook.front.client.user.UserClient;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.user.ActivateFailException;
import store.buzzbook.front.common.exception.user.AddressMaxCountException;
import store.buzzbook.front.common.exception.user.DeactivatedUserException;
import store.buzzbook.front.common.exception.user.DormantUserException;
import store.buzzbook.front.common.exception.user.PasswordIncorrectException;
import store.buzzbook.front.common.exception.user.PasswordNotConfirmedException;
import store.buzzbook.front.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.front.common.exception.user.UserNotFoundException;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.coupon.CouponResponse;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.dto.coupon.constant.CouponStatus;
import store.buzzbook.front.dto.point.PointLogResponse;
import store.buzzbook.front.dto.user.AddressInfoResponse;
import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.CreateAddressRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.Grade;
import store.buzzbook.front.dto.user.GradeName;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserApiRequest;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.UpdateAddressRequest;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.impl.UserServiceImpl;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserClient userClient;
	@Mock
	private JwtService jwtService;
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;


	private Grade grade;
	private UserInfo userInfo;
	private LoginUserResponse loginUserResponse;
	private String password;
	private RegisterUserRequest registerUserRequest;
	private DeactivateUserRequest deactivateUserRequest;
	private UpdateUserRequest updateUserRequest;
	private ChangePasswordRequest changePasswordRequest;
	private String newPassword;
	private String dormantToken;
	private CouponResponse couponResponse;
	private Page<PointLogResponse> pointLogResponseList;
	private List<AddressInfoResponse> addressList;
	private UpdateAddressRequest updateAddressRequest;
	private CreateAddressRequest createAddressRequest;

	@BeforeEach
	void setUp() {
		password = "password";
		dormantToken = "dormantoken";
		newPassword = "newPassword";

		grade = Grade.builder()
			.id(1)
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		userInfo = UserInfo.builder()
			.loginId("testid00000000")
			.name("john doe")
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(LocalDate.now().minusMonths(1))
			.id(1L)
			.point(132)
			.grade(grade)
			.isAdmin(false).build();

		loginUserResponse = new LoginUserResponse(
			userInfo.getLoginId(),
			password,
			userInfo.isAdmin()
		);

		registerUserRequest = new RegisterUserRequest(
			userInfo.getLoginId(),
			password,
			password,
			userInfo.getName(),
			userInfo.getContactNumber(),
			userInfo.getEmail(),
			true,
			userInfo.getBirthday()
		);

		deactivateUserRequest = new DeactivateUserRequest(
			password,
			"why"
		);

		updateUserRequest = new UpdateUserRequest(
			userInfo.getName(),
			userInfo.getContactNumber(),
			userInfo.getEmail()
		);

		changePasswordRequest = new ChangePasswordRequest(
			password,
			newPassword,
			newPassword
		);

		CouponTypeResponse couponTypeResponse = new CouponTypeResponse(1, "쿠폰 타입");

		CouponPolicyResponse couponPolicyResponse = new CouponPolicyResponse(
			1,
			"여름 할인",
			"비율",
			0.15,
			0,
			1000,
			5000,
			30,
			LocalDate.of(2024, 7, 1),
			LocalDate.of(2024, 7, 31),
			false,
			couponTypeResponse
		);

		couponResponse = new CouponResponse(
			1L,
			LocalDate.now(),
			LocalDate.now().plusMonths(1),
			CouponStatus.AVAILABLE,
			couponPolicyResponse
		);

		pointLogResponseList = new PageImpl<>(List.of(new PointLogResponse(
			LocalDateTime.now().minusDays(1),
			"어쩌고",
			100,
			1100
		), new PointLogResponse(
			LocalDateTime.now(),
			"저쩌고",
			1100,
			2200
		)));

		addressList = List.of(new AddressInfoResponse(
			2L, "test address",
			"test detail",
			12342,
			"test na",
			"wow"
		), AddressInfoResponse.builder()
				.id(3L)
				.address("test address2")
				.detail("test detail2")
				.zipcode(12242)
				.alias("wo2w")
				.nation("test na2")
			.build()
		);

		createAddressRequest = CreateAddressRequest.builder()
			.address("도로명주소")
			.alias("별칭")
			.nation("국가")
			.zipcode(12345)
			.detail("상세주소").build();

		updateAddressRequest = new UpdateAddressRequest(
			2L, createAddressRequest.address(),
			createAddressRequest.detail(),
			createAddressRequest.zipcode(),
			createAddressRequest.nation(),
			createAddressRequest.alias()
		);

	}


	@Test
	@DisplayName("회원가입 요청 성공")
	void testRegisterUserSuccess() {
		Mockito.when(userClient.registerUser(Mockito.any(RegisterUserApiRequest.class))).thenReturn(ResponseEntity.ok().build());

		userService.registerUser(registerUserRequest);

		Mockito.verify(userClient, Mockito.times(1)).registerUser(Mockito.any(RegisterUserApiRequest.class));
	}

	@Test
	@DisplayName("회원가입 요청 중 비밀번호 확인 실패")
	void testRegisterUserPasswordNotConfirmed() {
		RegisterUserRequest invalidRequest = new RegisterUserRequest(
			registerUserRequest.loginId(),
			registerUserRequest.password(),
			"incorrect",
			registerUserRequest.name(),
			registerUserRequest.contactNumber(),
			registerUserRequest.email(),
			registerUserRequest.emailVerified(),
			registerUserRequest.birthday()
		);

		Assertions.assertThrowsExactly(PasswordNotConfirmedException.class, () -> {
			userService.registerUser(invalidRequest);
		});

		Mockito.verify(userClient, Mockito.never()).registerUser(Mockito.any(RegisterUserApiRequest.class));
	}

	@Test
	@DisplayName("회원가입 요청 중복 에러")
	void testRegisterUserShouldBadRequest() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).registerUser(Mockito.any(RegisterUserApiRequest.class));

		Assertions.assertThrowsExactly(UserAlreadyExistsException.class,()->userService.registerUser(registerUserRequest));

		Mockito.verify(userClient, Mockito.times(1)).registerUser(Mockito.any(RegisterUserApiRequest.class));
	}

	@Test
	@DisplayName("로그인 요청 성공")
	void testRequestLoginSuccess() {
		Mockito.when(userClient.requestLogin(Mockito.anyString())).thenReturn(ResponseEntity.ok(loginUserResponse));

		LoginUserResponse response = userService.requestLogin("loginId");

		Assertions.assertEquals(loginUserResponse, response);
		Assertions.assertEquals(loginUserResponse.loginId(), response.loginId());
		Assertions.assertEquals(loginUserResponse.password(), response.password());
		Assertions.assertEquals(loginUserResponse.isAdmin(), response.isAdmin());
		Mockito.verify(userClient, Mockito.times(1)).requestLogin(Mockito.anyString());
	}

	@Test
	@DisplayName("로그인 요청 중 탈퇴한 유저")
	void testRequestLoginDeactivatedUser() {
		Mockito.when(userClient.requestLogin(userInfo.getLoginId())).thenThrow(FeignException.Forbidden.class);

		Assertions.assertThrowsExactly(DeactivatedUserException.class, () ->
			userService.requestLogin(userInfo.getLoginId())
		);

		Mockito.verify(userClient, Mockito.times(1)).requestLogin(Mockito.anyString());
	}

	@Test
	@DisplayName("로그인 성공 요청 성공")
	void testSuccessLoginSuccess() {
		Mockito.when(userClient.successLogin(userInfo.getLoginId())).thenReturn(ResponseEntity.ok(userInfo));

		UserInfo response = userService.successLogin(userInfo.getLoginId());

		Assertions.assertEquals(userInfo, response);
		Mockito.verify(userClient, Mockito.times(1)).successLogin(userInfo.getLoginId());
	}

	@Test
	@DisplayName("로그인 성공 요청 중 휴면 계정")
	void testSuccessLoginDormantUser() {
		Mockito.when(userClient.successLogin(userInfo.getLoginId())).thenThrow(FeignException.NotAcceptable.class);
		Mockito.when(jwtService.getDormantToken(userInfo.getLoginId())).thenReturn(dormantToken);

		Assertions.assertThrowsExactly(DormantUserException.class, () -> {
			userService.successLogin(userInfo.getLoginId());
		});

		Mockito.verify(userClient, Mockito.times(1)).successLogin(userInfo.getLoginId());
		Mockito.verify(jwtService, Mockito.times(1)).getDormantToken(userInfo.getLoginId());
	}

	@Test
	@DisplayName("유저 정보 가져오기 요청 성공")
	void testGetUserInfoSuccess() {
		Mockito.when(userClient.getUserInfo()).thenReturn(ResponseEntity.ok(userInfo));

		UserInfo response = userService.getUserInfo(userInfo.getId());

		Assertions.assertEquals(userInfo, response);
		Assertions.assertEquals(grade.getName(),response.getGrade().getName());
		Assertions.assertEquals(grade.getId(), response.getGrade().getId());
		Assertions.assertEquals(grade.getBenefit(), response.getGrade().getBenefit());
		Assertions.assertEquals(grade.getStandard(), response.getGrade().getStandard());
		Assertions.assertEquals(132, response.getPoint());
		Mockito.verify(userClient, Mockito.times(1)).getUserInfo();
	}

	@Test
	@DisplayName("유저 정보 가져오기 요청 유저 없음")
	void testGetUserInfoUserNotFound() {
		Mockito.when(userClient.getUserInfo()).thenThrow(FeignException.BadRequest.class);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.getUserInfo(userInfo.getId());
		});

		Mockito.verify(userClient, Mockito.times(1)).getUserInfo();
	}

	@Test
	@DisplayName("유저 탈퇴 성공")
	void testDeactivateSuccess() {
		Mockito.when(userClient.deactivateUser(deactivateUserRequest)).thenReturn(ResponseEntity.ok().build());

		userService.deactivate(userInfo.getId(), deactivateUserRequest);

		Mockito.verify(userClient, Mockito.times(1)).deactivateUser(deactivateUserRequest);
	}

	@Test
	@DisplayName("유저 탈퇴 비밀번호 인증 실패")
	void testDeactivatePasswordIncorrect() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).deactivateUser(deactivateUserRequest);

		Assertions.assertThrowsExactly(PasswordIncorrectException.class, () -> {
			userService.deactivate(1L, deactivateUserRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).deactivateUser(deactivateUserRequest);
	}

	@Test
	@DisplayName("유저 정보 수정 성공")
	void testUpdateUserInfoSuccess() {
		Mockito.when(userClient.updateUser(updateUserRequest)).thenReturn(ResponseEntity.ok().build());

		userService.updateUserInfo(1L, updateUserRequest);

		Mockito.verify(userClient, Mockito.times(1)).updateUser(updateUserRequest);
	}

	@Test
	@DisplayName("유저 정보 수정 중 유저 없음")
	void testUpdateUserInfoUserNotFound() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).updateUser(updateUserRequest);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.updateUserInfo(userInfo.getId(), updateUserRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).updateUser(updateUserRequest);
	}

	@Test
	@DisplayName("유저 비밀번호 변경 성공")
	void testChangePasswordSuccess() {
		Mockito.when(userClient.changePassword(changePasswordRequest)).thenReturn(ResponseEntity.ok().build());
		Assertions.assertNotEquals(changePasswordRequest.getOldPassword(),changePasswordRequest.getNewPassword());
		Assertions.assertEquals(changePasswordRequest.getNewPassword(),changePasswordRequest.getConfirmPassword());

		userService.changePassword(userInfo.getId(), changePasswordRequest);
		Assertions.assertNotEquals(changePasswordRequest.getNewPassword(), changePasswordRequest.getConfirmPassword());

		Mockito.verify(userClient, Mockito.times(1)).changePassword(changePasswordRequest);
	}

	@Test
	@DisplayName("유저 비밀번호 변경 중 비밀번호 확인 실패")
	void testChangePasswordPasswordNotConfirmed() {
		ChangePasswordRequest invalidRequest = new ChangePasswordRequest("oldPassword", "newPassword", "differentPassword");

		Assertions.assertThrowsExactly(PasswordNotConfirmedException.class, () -> {
			userService.changePassword(userInfo.getId(), invalidRequest);
		});

		Mockito.verify(userClient, Mockito.never()).changePassword(Mockito.any(ChangePasswordRequest.class));
	}

	@Test
	@DisplayName("유저 비밀번호 변경 중 비밀번호 인증 실패")
	void testChangePasswordPasswordIncorrect() {
		Mockito.when(userClient.changePassword(changePasswordRequest)).thenThrow(FeignException.BadRequest.class);

		Assertions.assertThrowsExactly(PasswordIncorrectException.class, () -> {
			userService.changePassword(userInfo.getId(), changePasswordRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).changePassword(changePasswordRequest);
	}

	@Test
	@DisplayName("유저 쿠폰 리스트 가져오기")
	void testGetUserCouponsSuccess() {
		Mockito.when(userClient.getUserCoupons(Mockito.anyString())).thenReturn(List.of(couponResponse));

		List<CouponResponse> response = userService.getUserCoupons("ACTIVE");

		Assertions.assertEquals(List.of(couponResponse), response);
		Mockito.verify(userClient, Mockito.times(1)).getUserCoupons(Mockito.anyString());
	}

	@Test
	@DisplayName("유저 쿠폰 리스트 가져오기 중 쿠폰 없음")
	void testGetUserCouponsNotFound() {
		Mockito.when(userClient.getUserCoupons(Mockito.anyString())).thenThrow(FeignException.NotFound.class);

		List<CouponResponse> response = userService.getUserCoupons("ACTIVE");

		Assertions.assertTrue(response.isEmpty());
		Mockito.verify(userClient, Mockito.times(1)).getUserCoupons(Mockito.anyString());
	}

	@Test
	@DisplayName("유저 포인트 가져오기")
	void testGetUserPoints() {
		Pageable pageable = PageRequest.of(1, 10);
		Mockito.when(userClient.getPointLogs(pageable)).thenReturn(pointLogResponseList);

		Page<PointLogResponse> response = userService.getUserPoints(pageable);

		Assertions.assertEquals(pointLogResponseList, response);
		Mockito.verify(userClient, Mockito.times(1)).getPointLogs(pageable);
	}

	@Test
	@DisplayName("주소 리스트 가져오기")
	void testGetAddressListSuccess() {
		Mockito.when(userClient.getAddressList()).thenReturn(ResponseEntity.ok(addressList));

		List<AddressInfoResponse> response = userService.getAddressList();

		Assertions.assertEquals(addressList, response);
		Assertions.assertEquals(addressList.size(), response.size());
		Assertions.assertAll(()->{
			for(int i = 0; i < addressList.size(); i++) {
				AddressInfoResponse addressInfoResponse = response.get(i);

				Assertions.assertEquals(addressInfoResponse.address(),addressList.get(i).address());
				Assertions.assertEquals(addressInfoResponse.id(), addressList.get(i).id());
				Assertions.assertEquals(addressInfoResponse.alias(), addressList.get(i).alias());
				Assertions.assertEquals(addressInfoResponse.detail(),addressList.get(i).detail());
				Assertions.assertEquals(addressInfoResponse.nation(),addressList.get(i).nation());
				Assertions.assertEquals(addressInfoResponse.zipcode(), addressList.get(i).zipcode());
				}
			});
		Mockito.verify(userClient, Mockito.times(1)).getAddressList();
	}

	@Test
	@DisplayName("주소 리스트 가져오기 중 유저 없음")
	void testGetAddressListUserNotFound() {
		Mockito.when(userClient.getAddressList()).thenThrow(FeignException.BadRequest.class);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.getAddressList();
		});

		Mockito.verify(userClient, Mockito.times(1)).getAddressList();
	}

	@Test
	@DisplayName("주소 리스트 가져오기 중 유저 인증 실패")
	void testGetAddressListAuthorizeFail() {
		Mockito.when(userClient.getAddressList()).thenThrow(FeignException.Unauthorized.class);

		Assertions.assertThrowsExactly(AuthorizeFailException.class, () -> {
			userService.getAddressList();
		});

		Mockito.verify(userClient, Mockito.times(1)).getAddressList();
	}

	@Test
	@DisplayName("주소 수정")
	void testUpdateAddressSuccess() {
		Mockito.when(userClient.updateAddress(updateAddressRequest)).thenReturn(ResponseEntity.ok().build());

		userService.updateAddress(updateAddressRequest);

		Mockito.verify(userClient, Mockito.times(1)).updateAddress(updateAddressRequest);
	}

	@Test
	@DisplayName("주소 수정 중 유저 발견 실패")
	void testUpdateAddressUserNotFound() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).updateAddress(updateAddressRequest);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.updateAddress(updateAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).updateAddress(updateAddressRequest);
	}

	@Test
	@DisplayName("주소 수정 중 인증실패")
	void testUpdateAddressAuthorizeFail() {
		Mockito.doThrow(FeignException.Unauthorized.class).when(userClient).updateAddress(updateAddressRequest);

		Assertions.assertThrowsExactly(AuthorizeFailException.class, () -> {
			userService.updateAddress(updateAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).updateAddress(updateAddressRequest);
	}

	@Test
	@DisplayName("주소 삭제")
	void testDeleteAddressSuccess() {
		Mockito.when(userClient.deleteAddress(updateAddressRequest.id())).thenReturn(ResponseEntity.ok().build());

		userService.deleteAddress(updateAddressRequest.id());

		Mockito.verify(userClient, Mockito.times(1)).deleteAddress(updateAddressRequest.id());
	}

	@Test
	@DisplayName("주소 삭제 유저 발견 실패")
	void testDeleteAddressUserNotFound() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).deleteAddress(updateAddressRequest.id());

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.deleteAddress(updateAddressRequest.id());
		});

		Mockito.verify(userClient, Mockito.times(1)).deleteAddress(updateAddressRequest.id());
	}

	@Test
	@DisplayName("주소 삭제 중 인증 실패")
	void testDeleteAddressAuthorizeFail() {
		Mockito.doThrow(FeignException.Unauthorized.class).when(userClient).deleteAddress(updateAddressRequest.id());

		Assertions.assertThrowsExactly(AuthorizeFailException.class, () -> {
			userService.deleteAddress(updateAddressRequest.id());
		});

		Mockito.verify(userClient, Mockito.times(1)).deleteAddress(updateAddressRequest.id());
	}

	@Test
	@DisplayName("주소 생성")
	void testCreateAddressSuccess() {
		Mockito.when(userClient.createAddress(createAddressRequest)).thenReturn(ResponseEntity.ok().build());

		userService.createAddress(createAddressRequest);

		Mockito.verify(userClient, Mockito.times(1)).createAddress(createAddressRequest);
	}

	@Test
	@DisplayName("주소 생성 중 유저 발견 실패")
	void testCreateAddressUserNotFound() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).createAddress(createAddressRequest);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.createAddress(createAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).createAddress(createAddressRequest);
	}

	@Test
	@DisplayName("주소 생성 중 유저 인증 실패")
	void testCreateAddressAuthorizeFail() {
		Mockito.doThrow(FeignException.Unauthorized.class).when(userClient).createAddress(createAddressRequest);

		Assertions.assertThrowsExactly(AuthorizeFailException.class, () -> {
			userService.createAddress(createAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).createAddress(createAddressRequest);
	}

	@Test
	@DisplayName("주소 생성 중 한계 초과")
	void testCreateAddressMaxCountException() {
		Mockito.doThrow(FeignException.NotAcceptable.class).when(userClient).createAddress(createAddressRequest);

		Assertions.assertThrowsExactly(AddressMaxCountException.class, () -> {
			userService.createAddress(createAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).createAddress(createAddressRequest);
	}

	@Test
	@DisplayName("유저 활성화")
	void testActivateSuccess() {
		Mockito.when(userClient.activateUser(userInfo.getLoginId())).thenReturn(ResponseEntity.ok().build());

		userService.activate(userInfo.getLoginId());

		Mockito.verify(userClient, Mockito.times(1)).activateUser(Mockito.anyString());
	}

	@Test
	@DisplayName("유저 활성화 중 이미 활성화된 유저")
	void testActivateUserAlreadyActive() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).activateUser(userInfo.getLoginId());

		Assertions.assertThrowsExactly(ActivateFailException.class, () -> {
			userService.activate(userInfo.getLoginId());
		});

		Mockito.verify(userClient, Mockito.times(1)).activateUser(userInfo.getLoginId());
	}

	@Test
	@DisplayName("유저 활성화 중 탈퇴한 유저")
	void testActivate_Deactivate() {
		Mockito.doThrow(FeignException.Forbidden.class).when(userClient).activateUser(userInfo.getLoginId());

		Assertions.assertThrowsExactly(DeactivatedUserException.class, () -> {
			userService.activate(userInfo.getLoginId());
		});

		Mockito.verify(userClient, Mockito.times(1)).activateUser(userInfo.getLoginId());
	}

	@Test
	@DisplayName("제어되지 않은 오류들 확인")
	void testOtherExceptionThrow(){
		Mockito.doThrow(FeignException.InternalServerError.class).when(userClient).activateUser(userInfo.getLoginId());
		Mockito.doThrow(FeignException.InternalServerError.class).when(userClient).createAddress(createAddressRequest);
		Mockito.doThrow(FeignException.InternalServerError.class).when(userClient).deleteAddress(updateAddressRequest.id());
		Mockito.doThrow(FeignException.InternalServerError.class).when(userClient).updateAddress(updateAddressRequest);
		Mockito.doThrow(FeignException.InternalServerError.class).when(userClient).getAddressList();

		Assertions.assertThrows(FeignException.InternalServerError.class,() -> {
			userService.activate(userInfo.getLoginId());
		});
		Assertions.assertThrows(FeignException.InternalServerError.class,() -> {
			userService.createAddress(createAddressRequest);
		});
		Assertions.assertThrows(FeignException.InternalServerError.class,() -> {
			userService.deleteAddress(updateAddressRequest.id());
		});
		Assertions.assertThrows(FeignException.InternalServerError.class,() -> {
			userService.updateAddress(updateAddressRequest);
		});
		Assertions.assertThrows(FeignException.InternalServerError.class,() -> {
			userService.getAddressList();
		});
	}

}

