package store.buzzbook.front.service.user.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.user.UserRestClient;
import store.buzzbook.front.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.service.user.UserService;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserRestClient userRestClient;


	public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
		//todo 더하기
		RegisterUserResponse registerUserResponse;

		try{
			registerUserResponse = userRestClient.registerUser(registerUserRequest);
		}catch (UserAlreadyExistsException e){
			log.warn(e.getMessage());

		}








		return null;// registerUserResponse;
	}

}
