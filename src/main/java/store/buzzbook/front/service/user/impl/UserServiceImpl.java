package store.buzzbook.front.service.user.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.user.UserRestClient;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.service.user.UserService;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
	private final UserRestClient userRestClient;


	public RegisterUserResponse registerUser(RegisterUserRequest request) {
		//todo 더하기
		return userRestClient.registerUser(request);
	}

}
