package store.buzzbook.front.service.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

@Disabled
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
		dormantToken = "dormantoken";
		newPassword = "newPassword";

		grade = Grade.builder()
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
		), new AddressInfoResponse(
			3L,
			"test address2",
			"test detail2",
			12242,
			"test na2",
			"wo2w"
		));

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
		Mockito.doNothing().when(userClient).registerUser(Mockito.any(RegisterUserApiRequest.class));

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
	void testRequestLoginSuccess() {
		Mockito.when(userClient.requestLogin(Mockito.anyString())).thenReturn(ResponseEntity.ok(loginUserResponse));

		LoginUserResponse response = userService.requestLogin("loginId");

		Assertions.assertEquals(loginUserResponse, response);
		Mockito.verify(userClient, Mockito.times(1)).requestLogin(Mockito.anyString());
	}

	@Test
	void testRequestLoginDeactivatedUser() {
		Mockito.when(userClient.requestLogin(userInfo.getLoginId())).thenThrow(FeignException.Forbidden.class);

		Assertions.assertThrowsExactly(DeactivatedUserException.class, () ->
			userService.requestLogin(userInfo.getLoginId())
		);

		Mockito.verify(userClient, Mockito.times(1)).requestLogin(Mockito.anyString());
	}

	@Test
	void testSuccessLoginSuccess() {
		Mockito.when(userClient.successLogin(userInfo.getLoginId())).thenReturn(ResponseEntity.ok(userInfo));

		UserInfo response = userService.successLogin(userInfo.getLoginId());

		Assertions.assertEquals(userInfo, response);
		Mockito.verify(userClient, Mockito.times(1)).successLogin(userInfo.getLoginId());
	}

	@Test
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
	void testGetUserInfo_Success() {
		Mockito.when(userClient.getUserInfo()).thenReturn(ResponseEntity.ok(userInfo));

		UserInfo response = userService.getUserInfo(userInfo.getId());

		Assertions.assertEquals(userInfo, response);
		Mockito.verify(userClient, Mockito.times(1)).getUserInfo();
	}

	@Test
	void testGetUserInfo_UserNotFound() {
		Mockito.when(userClient.getUserInfo()).thenThrow(FeignException.BadRequest.class);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.getUserInfo(userInfo.getId());
		});

		Mockito.verify(userClient, Mockito.times(1)).getUserInfo();
	}

	@Test
	void testDeactivate_Success() {
		Mockito.doNothing().when(userClient).deactivateUser(deactivateUserRequest);

		userService.deactivate(userInfo.getId(), deactivateUserRequest);

		Mockito.verify(userClient, Mockito.times(1)).deactivateUser(deactivateUserRequest);
	}

	@Test
	void testDeactivate_PasswordIncorrect() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).deactivateUser(deactivateUserRequest);

		Assertions.assertThrowsExactly(PasswordIncorrectException.class, () -> {
			userService.deactivate(1L, deactivateUserRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).deactivateUser(deactivateUserRequest);
	}

	@Test
	void testUpdateUserInfo_Success() {
		Mockito.doNothing().when(userClient).updateUser(updateUserRequest);

		userService.updateUserInfo(1L, updateUserRequest);

		Mockito.verify(userClient, Mockito.times(1)).updateUser(updateUserRequest);
	}

	@Test
	void testUpdateUserInfo_UserNotFound() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).updateUser(updateUserRequest);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.updateUserInfo(userInfo.getId(), updateUserRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).updateUser(updateUserRequest);
	}

	@Test
	void testChangePassword_Success() {
		Mockito.doNothing().when(userClient).changePassword(changePasswordRequest);

		userService.changePassword(userInfo.getId(), changePasswordRequest);

		Mockito.verify(userClient, Mockito.times(1)).changePassword(changePasswordRequest);
	}

	@Test
	void testChangePassword_PasswordNotConfirmed() {
		ChangePasswordRequest invalidRequest = new ChangePasswordRequest("oldPassword", "newPassword", "differentPassword");

		Assertions.assertThrowsExactly(PasswordNotConfirmedException.class, () -> {
			userService.changePassword(userInfo.getId(), invalidRequest);
		});

		Mockito.verify(userClient, Mockito.never()).changePassword(Mockito.any(ChangePasswordRequest.class));
	}

	@Test
	void testChangePasswordPasswordIncorrect() {
		ChangePasswordRequest invalidRequest = new ChangePasswordRequest("oldPassword", "newPassword", "differentPassword");
		Mockito.when(userClient.changePassword(invalidRequest)).thenThrow(FeignException.BadRequest.class);

		Assertions.assertThrowsExactly(PasswordIncorrectException.class, () -> {
			userService.changePassword(userInfo.getId(), invalidRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).changePassword(Mockito.any(ChangePasswordRequest.class));
	}

	@Test
	void testGetUserCoupons_Success() {
		Mockito.when(userClient.getUserCoupons(Mockito.anyString())).thenReturn(List.of(couponResponse));

		List<CouponResponse> response = userService.getUserCoupons("ACTIVE");

		Assertions.assertEquals(List.of(couponResponse), response);
		Mockito.verify(userClient, Mockito.times(1)).getUserCoupons(Mockito.anyString());
	}

	@Test
	void testGetUserCoupons_NotFound() {
		Mockito.when(userClient.getUserCoupons(Mockito.anyString())).thenThrow(FeignException.NotFound.class);

		List<CouponResponse> response = userService.getUserCoupons("ACTIVE");

		Assertions.assertTrue(response.isEmpty());
		Mockito.verify(userClient, Mockito.times(1)).getUserCoupons(Mockito.anyString());
	}

	@Test
	void testGetUserPoints() {
		Pageable pageable = PageRequest.of(1, 10);
		Mockito.when(userClient.getPointLogs(pageable)).thenReturn(pointLogResponseList);

		Page<PointLogResponse> response = userService.getUserPoints(pageable);

		Assertions.assertEquals(pointLogResponseList, response);
		Mockito.verify(userClient, Mockito.times(1)).getPointLogs(pageable);
	}

	@Test
	void testGetAddressList_Success() {
		Mockito.when(userClient.getAddressList()).thenReturn(ResponseEntity.ok(addressList));

		List<AddressInfoResponse> response = userService.getAddressList();

		Assertions.assertEquals(addressList, response);
		Mockito.verify(userClient, Mockito.times(1)).getAddressList();
	}

	@Test
	void testGetAddressList_UserNotFound() {
		Mockito.when(userClient.getAddressList()).thenThrow(FeignException.BadRequest.class);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.getAddressList();
		});

		Mockito.verify(userClient, Mockito.times(1)).getAddressList();
	}

	@Test
	void testGetAddressList_AuthorizeFail() {
		Mockito.when(userClient.getAddressList()).thenThrow(FeignException.Unauthorized.class);

		Assertions.assertThrowsExactly(AuthorizeFailException.class, () -> {
			userService.getAddressList();
		});

		Mockito.verify(userClient, Mockito.times(1)).getAddressList();
	}

	@Test
	void testUpdateAddress_Success() {
		Mockito.doNothing().when(userClient).updateAddress(updateAddressRequest);

		userService.updateAddress(updateAddressRequest);

		Mockito.verify(userClient, Mockito.times(1)).updateAddress(updateAddressRequest);
	}

	@Test
	void testUpdateAddress_UserNotFound() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).updateAddress(updateAddressRequest);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.updateAddress(updateAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).updateAddress(updateAddressRequest);
	}

	@Test
	void testUpdateAddress_AuthorizeFail() {
		Mockito.doThrow(FeignException.Unauthorized.class).when(userClient).updateAddress(updateAddressRequest);

		Assertions.assertThrowsExactly(AuthorizeFailException.class, () -> {
			userService.updateAddress(updateAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).updateAddress(updateAddressRequest);
	}

	@Test
	void testDeleteAddress_Success() {
		Mockito.doNothing().when(userClient).deleteAddress(updateAddressRequest.id());

		userService.deleteAddress(updateAddressRequest.id());

		Mockito.verify(userClient, Mockito.times(1)).deleteAddress(updateAddressRequest.id());
	}

	@Test
	void testDeleteAddress_UserNotFound() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).deleteAddress(updateAddressRequest.id());

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.deleteAddress(updateAddressRequest.id());
		});

		Mockito.verify(userClient, Mockito.times(1)).deleteAddress(updateAddressRequest.id());
	}

	@Test
	void testDeleteAddress_AuthorizeFail() {
		Mockito.doThrow(FeignException.Unauthorized.class).when(userClient).deleteAddress(updateAddressRequest.id());

		Assertions.assertThrowsExactly(AuthorizeFailException.class, () -> {
			userService.deleteAddress(updateAddressRequest.id());
		});

		Mockito.verify(userClient, Mockito.times(1)).deleteAddress(updateAddressRequest.id());
	}

	@Test
	void testCreateAddress_Success() {
		Mockito.doNothing().when(userClient).createAddress(createAddressRequest);

		userService.createAddress(createAddressRequest);

		Mockito.verify(userClient, Mockito.times(1)).createAddress(createAddressRequest);
	}

	@Test
	void testCreateAddress_UserNotFound() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).createAddress(createAddressRequest);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> {
			userService.createAddress(createAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).createAddress(createAddressRequest);
	}

	@Test
	void testCreateAddress_AuthorizeFail() {
		Mockito.doThrow(FeignException.Unauthorized.class).when(userClient).createAddress(createAddressRequest);

		Assertions.assertThrowsExactly(AuthorizeFailException.class, () -> {
			userService.createAddress(createAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).createAddress(createAddressRequest);
	}

	@Test
	void testCreateAddress_MaxCountException() {
		Mockito.doThrow(FeignException.NotAcceptable.class).when(userClient).createAddress(createAddressRequest);

		Assertions.assertThrowsExactly(AddressMaxCountException.class, () -> {
			userService.createAddress(createAddressRequest);
		});

		Mockito.verify(userClient, Mockito.times(1)).createAddress(createAddressRequest);
	}

	@Test
	void testActivate_Success() {
		Mockito.doNothing().when(userClient).activateUser(userInfo.getLoginId());

		userService.activate(userInfo.getLoginId());

		Mockito.verify(userClient, Mockito.times(1)).activateUser(Mockito.anyString());
	}

	@Test
	void testActivate_UserAlreadyActive() {
		Mockito.doThrow(FeignException.BadRequest.class).when(userClient).activateUser(userInfo.getLoginId());

		Assertions.assertThrowsExactly(ActivateFailException.class, () -> {
			userService.activate(userInfo.getLoginId());
		});

		Mockito.verify(userClient, Mockito.times(1)).activateUser(userInfo.getLoginId());
	}

	@Test
	void testActivate_Deactivate() {
		Mockito.doThrow(FeignException.Forbidden.class).when(userClient).activateUser(userInfo.getLoginId());

		Assertions.assertThrowsExactly(DeactivatedUserException.class, () -> {
			userService.activate(userInfo.getLoginId());
		});

		Mockito.verify(userClient, Mockito.times(1)).activateUser(userInfo.getLoginId());
	}


}

