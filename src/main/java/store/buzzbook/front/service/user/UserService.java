package store.buzzbook.front.service.user;

import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.dto.user.UserInfo;

public interface UserService {
	RegisterUserResponse registerUser(RegisterUserRequest request);
	LoginUserResponse requestLogin(String loginId);
	UserInfo successLogin(String loginId);


}
