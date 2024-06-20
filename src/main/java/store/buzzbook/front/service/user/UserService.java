package store.buzzbook.front.service.user;

import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;

public interface UserService {
	RegisterUserResponse registerUser(RegisterUserRequest request);

}
