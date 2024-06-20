package store.buzzbook.front.client.user;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;

@Slf4j
@Component
public class UserRestClient {

	public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
		log.info("Registering user: {}", registerUserRequest);

		RestClient restClient = RestClient.builder().baseUrl("http://localhost:8080/api/account").build();


		RegisterUserResponse registerUserResponse = restClient.post().uri("/register")
			.body(registerUserRequest)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (request, response)->{
				Integer status = response.getStatusCode().value();

				if(status.equals(400)) {
					log.warn("회원가입 실패 : 아이디가 중복됩니다. id : {}", registerUserRequest.loginId());
					throw new UserAlreadyExistsException(registerUserRequest.loginId());
				}

			})
			.onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
				Integer status = response.getStatusCode().value();

				if(status.equals(500)) {
					log.warn("회원가입 실패 : 알 수 없는 오류 : {}", registerUserRequest.loginId());
				}
			})).body(RegisterUserResponse.class);


		if(Objects.isNull(registerUserResponse)) {
			log.warn("회원가입 : 알 수 없는 오류");
			return null;
		}

		log.info("회원가입 통신 완료");

		return registerUserResponse;
	}
}
