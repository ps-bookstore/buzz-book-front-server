package store.buzzbook.front.controller.user;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.coupon.CouponResponse;
import store.buzzbook.front.dto.coupon.CouponTypeResponse;
import store.buzzbook.front.dto.coupon.constant.CouponStatus;
import store.buzzbook.front.dto.point.PointLogResponse;
import store.buzzbook.front.dto.user.AddressInfoResponse;
import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.Grade;
import store.buzzbook.front.dto.user.GradeName;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.UserService;

@ActiveProfiles("test")
@WebMvcTest({MyPageController.class, DeactivateRestController.class})
class MyPageControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private HttpServletRequest request;
	@MockBean
	private CartInterceptor cartInterceptor;
	@MockBean
	private UserService userService;
	@MockBean
	private JwtService jwtService;

	private UserInfo userInfo;
	private List<CouponResponse> couponResponses;
	private Page<PointLogResponse> pointLogResponses;
	private List<AddressInfoResponse> addressInfoResponses;
	private Grade grade;
	private UpdateUserRequest updateUserRequest;
	private String password;
	private String newPassword;

	@BeforeEach
	void setUp() {
		password = "password";
		newPassword = "newPassword";

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
			.contactNumber("01000001111")
			.birthday(LocalDate.now().minusMonths(1))
			.id(1L)
			.point(132)
			.grade(grade)
			.isAdmin(false).build();

		couponResponses = List.of(new CouponResponse(
			1L,
			LocalDate.now(),
			LocalDate.now().plusMonths(1),
			CouponStatus.AVAILABLE,
			couponPolicyResponse
		), new CouponResponse(2L,
			LocalDate.now().minusMonths(2),
			LocalDate.now().plusMonths(2),
			CouponStatus.AVAILABLE,
			couponPolicyResponse));

		pointLogResponses = new PageImpl<>(List.of(new PointLogResponse(
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

		addressInfoResponses = List.of(new AddressInfoResponse(
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

		updateUserRequest =
			new UpdateUserRequest(
				userInfo.getName(),
				userInfo.getContactNumber(),
				userInfo.getEmail()
			);
		request.setAttribute(JwtService.USER_ID, userInfo.getId());

		lenient().when(request.getAttribute(JwtService.USER_ID)).thenReturn(userInfo.getId());

		when(jwtService.getUserIdFromJwt(any(HttpServletRequest.class), any(HttpServletResponse.class))).thenReturn(userInfo.getId());
	}

	@WithMockUser
	@Test
	void testMyPage() throws Exception {
		when(userService.getUserInfo(userInfo.getId())).thenReturn(userInfo);

		mockMvc.perform(get("/mypage").with(csrf())
				.with(dRequest -> {
						dRequest.setAttribute(JwtService.USER_ID, userInfo.getId());
						return dRequest;
					}
				))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("title", "마이페이지"))
			.andExpect(model().attribute("page", "mypage-index"))
			.andExpect(model().attribute("fragment", "myInfo"))
			.andExpect(model().attribute("user", userInfo));

		verify(userService, times(1)).getUserInfo(userInfo.getId());
	}

	@WithMockUser
	@Test
	void testDeactivateForm() throws Exception {
		mockMvc.perform(get("/mypage/deactivate").with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("title", "탈퇴"))
			.andExpect(model().attribute("page", "deactivate"));
	}

	@WithMockUser
	@Test
	void testEditForm() throws Exception {
		when(userService.getUserInfo(userInfo.getId())).thenReturn(userInfo);

		mockMvc.perform(get("/mypage/edit").with(csrf())
				.with(dRequest -> {
						dRequest.setAttribute(JwtService.USER_ID, userInfo.getId());
						return dRequest;
					}
				))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("title", "정보 수정"))
			.andExpect(model().attribute("page", "mypage-index"))
			.andExpect(model().attribute("fragment", "info-edit"))
			.andExpect(model().attribute("user", userInfo));

		verify(userService, times(1)).getUserInfo(userInfo.getId());
	}

	@WithMockUser
	@Test
	void testChangePasswordForm() throws Exception {
		mockMvc.perform(get("/mypage/password").with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("title", "비밀번호 변경"))
			.andExpect(model().attribute("page", "mypage-index"))
			.andExpect(model().attribute("fragment", "change-password"));
	}

	@WithMockUser
	@Test
	void testEdit() throws Exception {
		doNothing().when(userService).updateUserInfo(userInfo.getId(), updateUserRequest);

		mockMvc.perform(post("/mypage/edit").with(csrf())
				.with(dRequest -> {
						dRequest.setAttribute(JwtService.USER_ID, userInfo.getId());
						return dRequest;
					}
				)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", updateUserRequest.name())
				.param("email", updateUserRequest.email())
				.param("contactNumber", updateUserRequest.contactNumber()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage"));

		verify(userService, times(1)).updateUserInfo(eq(userInfo.getId()), any(UpdateUserRequest.class));
	}

	@WithMockUser
	@Test
	void testChangePassword() throws Exception {

		mockMvc.perform(post("/mypage/password").with(csrf())
				.with(dRequest -> {
						dRequest.setAttribute(JwtService.USER_ID, userInfo.getId());
						return dRequest;
					}
				)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("oldPassword",password)
				.param("newPassword",newPassword)
				.param("confirmPassword", newPassword))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage"));

		verify(userService, times(1)).changePassword(eq(userInfo.getId()), any(ChangePasswordRequest.class));
	}

	@WithMockUser
	@Test
	void testCoupons() throws Exception {
		when(userService.getUserCoupons("all")).thenReturn(couponResponses);

		mockMvc.perform(get("/mypage/coupons").with(csrf())
				.param("couponStatusName", "all"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("coupons", couponResponses))
			.andExpect(model().attribute("title", "쿠폰내역"))
			.andExpect(model().attribute("page", "mypage-index"))
			.andExpect(model().attribute("fragment", "mypage-coupons"));

		verify(userService, times(1)).getUserCoupons("all");
	}

	@WithMockUser
	@Test
	void testPoints() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		when(userService.getUserPoints(pageable)).thenReturn(pointLogResponses);

		mockMvc.perform(get("/mypage/points").with(csrf())
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("points", pointLogResponses))
			.andExpect(model().attribute("title", "포인트내역"))
			.andExpect(model().attribute("page", "mypage-index"))
			.andExpect(model().attribute("fragment", "mypage-points"));

		verify(userService, times(1)).getUserPoints(pageable);
	}

	@WithMockUser
	@Test
	void testGetAddressList() throws Exception {
		when(userService.getAddressList()).thenReturn(addressInfoResponses);

		mockMvc.perform(get("/mypage/addresses").with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("title", "주소관리"))
			.andExpect(model().attribute("page", "mypage-index"))
			.andExpect(model().attribute("fragment", "mypage-address"))
			.andExpect(model().attribute("addressList", addressInfoResponses));

		verify(userService, times(1)).getAddressList();
	}

	@WithMockUser
	@Test
	void testDeleteAddress() throws Exception {
		mockMvc.perform(delete("/mypage/addresses").with(csrf())
				.param("addressId", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/mypage/addresses"));

		verify(userService, times(1)).deleteAddress(1L);
	}

	@WithMockUser
	@Test
	void testPostDeactivate() throws Exception {
		DeactivateUserRequest deactivateUserRequest =
			new DeactivateUserRequest(
				password, "이유는6글자이상"
			);

		doNothing().when(userService).deactivate(userInfo.getId(),deactivateUserRequest);

		mockMvc.perform(post("/mypage/deactivate").with(csrf())
				.with(dRequest -> {
						dRequest.setAttribute(JwtService.USER_ID, userInfo.getId());
						return dRequest;
					}
				)
				.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(deactivateUserRequest)))
			.andExpect(status().isOk());

		verify(userService, times(1)).deactivate(anyLong(), any(DeactivateUserRequest.class));
	}
}
