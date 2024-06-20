package store.buzzbook.front.client.user;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.front.dto.user.RegisterUserApiRequest;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;

@Slf4j
@Component
public class UserRestClient {

	public RegisterUserResponse registerUser(RegisterUserApiRequest registerUserApiRequest) {
		log.info("Registering user: {}", registerUserApiRequest);

		RestClient restClient = RestClient.builder().baseUrl("http://localhost:8080/api/account").build();


		RegisterUserResponse registerUserResponse = restClient.post().uri("/register")
			.body(registerUserApiRequest)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (request, response)->{
				Integer status = response.getStatusCode().value();

				if(status.equals(400)) {
					log.warn("회원가입 실패 : 아이디가 중복됩니다. id : {}", registerUserApiRequest.loginId());
					throw new UserAlreadyExistsException(registerUserApiRequest.loginId());
				}

			})
			.onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
				Integer status = response.getStatusCode().value();

				if(status.equals(500)) {
					log.warn("회원가입 실패 : 알 수 없는 오류 : {}", registerUserApiRequest.loginId());
				}
			})).body(RegisterUserResponse.class);


		if(Objects.isNull(registerUserResponse)) {
			log.error("회원가입 : 알 수 없는 오류");
			return null;
		}

		log.info("회원가입 통신 완료");

		return registerUserResponse;
	}
}
