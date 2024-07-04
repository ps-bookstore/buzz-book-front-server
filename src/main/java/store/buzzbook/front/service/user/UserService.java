package store.buzzbook.front.service.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.front.dto.coupon.CouponResponse;
import store.buzzbook.front.dto.point.PointLogResponse;
import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;

public interface UserService {
	void registerUser(RegisterUserRequest request);

	LoginUserResponse requestLogin(String loginId);

	UserInfo successLogin(String loginId);

	UserInfo getUserInfo(Long userId);

	void deactivate(Long userId, DeactivateUserRequest deactivateUserRequest);

	void updateUserInfo(Long userId, UpdateUserRequest updateUserRequest);

	void changePassword(Long userId, ChangePasswordRequest changePasswordRequest);

	List<CouponResponse> getUserCoupons(String couponStatusName);

	Page<PointLogResponse> getUserPoints(Pageable pageable);
}
