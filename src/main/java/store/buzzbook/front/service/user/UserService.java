package store.buzzbook.front.service.user;

import store.buzzbook.front.dto.user.ChangePasswordRequest;
import store.buzzbook.front.dto.user.DeactivateUserRequest;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.dto.user.UpdateUserRequest;
import store.buzzbook.front.dto.user.UserInfo;

public interface UserService {
	RegisterUserResponse registerUser(RegisterUserRequest request);
	LoginUserResponse requestLogin(String loginId);
	UserInfo successLogin(String loginId);
	UserInfo getUserInfo(Long userId);
	void deactivate(Long userId,DeactivateUserRequest deactivateUserRequest);
	UserInfo updateUserInfo(Long userId,UpdateUserRequest updateUserRequest);
	void changePassword(Long userId,ChangePasswordRequest changePasswordRequest);

}
