package store.buzzbook.front.client.user;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.front.common.exception.user.UserNotFoundException;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.dto.user.RegisterUserApiRequest;
import store.buzzbook.front.dto.user.RegisterUserRequest;
import store.buzzbook.front.dto.user.RegisterUserResponse;
import store.buzzbook.front.dto.user.UserInfo;

@Slf4j
@Component
public class UserRestClient {
	private final String host;

	public UserRestClient(@Value("${api.core.port}") int port) {
		this.host = String.format("http://localhost:%d/api/account", port);
	}


	public RegisterUserResponse registerUser(RegisterUserApiRequest registerUserApiRequest) {


		log.info("Registering user: {}", registerUserApiRequest);

		RestClient restClient = RestClient.builder().baseUrl(host).build();

		RegisterUserResponse registerUserResponse = restClient.post().uri("/register")
			.body(registerUserApiRequest)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (request, response)->{
				log.warn("4xx 에러 발생 : {}, {}",response.getStatusCode(), response.getBody());

				Integer status = response.getStatusCode().value();

				if(status.equals(400)) {
					log.warn("회원가입 실패 : 아이디가 중복됩니다. id : {}", registerUserApiRequest.loginId());
					throw new UserAlreadyExistsException(registerUserApiRequest.loginId());
				}

			})
			.onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
				log.warn("5xx 에러 발생 : {}, {}",response.getStatusCode(), response.getBody());
				Integer status = response.getStatusCode().value();

				if(status.equals(500)) {
					log.warn("회원가입 실패 : 알 수 없는 오류 : {}", registerUserApiRequest.loginId());
				}
			})).body(RegisterUserResponse.class);

		log.info("회원가입 통신은 끝남");


		if(Objects.isNull(registerUserResponse)) {
			log.error("회원가입 : 알 수 없는 오류");
			return null;
		}

		log.info("회원가입 통신 완료");

		return registerUserResponse;
	}


	public LoginUserResponse requestLogin(String loginId) {
		log.info("로그인 요청 : {}", loginId);
		RestClient restClient = RestClient.builder().baseUrl(host).build();


		LoginUserResponse loginUserResponse = restClient.post().uri("/login")
			.body(loginId)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (request, response)->{
				log.warn("4xx 에러 발생 : {}, {}",response.getStatusCode(), response.getBody());
				Integer status = response.getStatusCode().value();

				if(status.equals(HttpStatus.BAD_REQUEST.value())) {
					log.warn("로그인 실패 : 등록된 아이디가 없거나 탈퇴한 회원입니다. id : {}", loginId);
					throw new UserNotFoundException(loginId);
				}
			})
			.onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
				log.warn("5xx 에러 발생 : {}, {}",response.getStatusCode(), response.getBody());
				Integer status = response.getStatusCode().value();

				if(status.equals(500)) {
					log.warn("로그인 실패 : 알 수 없는 오류 : {}", loginId);
				}
			})).body(LoginUserResponse.class);


		if(Objects.isNull(loginUserResponse)) {
			log.error("로그인 : 알 수 없는 오류");
			return null;
		}

		log.info("로그인 통신 완료");

		return loginUserResponse;

	}

	public UserInfo successLogin(String loginId) {
		log.info("로그인 성공 처리 : {}", loginId);
		RestClient restClient = RestClient.builder().baseUrl(host).build();


		UserInfo userInfo = restClient.patch().uri("/login")
			.body(loginId)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (request, response)->{
				log.warn("4xx 에러 발생 : {}, {}",response.getStatusCode(), response.getBody());
				Integer status = response.getStatusCode().value();

				if(status.equals(HttpStatus.BAD_REQUEST.value())) {
					log.warn("로그인 성공 처리 실패 : 등록된 아이디가 없습니다. id : {}", loginId);
					throw new UserNotFoundException(loginId);
				}

			})
			.onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
				log.warn("5xx 에러 발생 : {}, {}",response.getStatusCode(), response.getBody());
				Integer status = response.getStatusCode().value();

				if(status.equals(HttpStatus.INTERNAL_SERVER_ERROR.value())) {
					log.warn("로그인 성공 처리 실패 : 알 수 없는 오류 : {}", loginId);
				}
			})).body(UserInfo.class);


		if(Objects.isNull(userInfo)) {
			log.error("로그인 성공 처리 : 알 수 없는 오류");
			return null;
		}

		log.info("로그인 성공 처리 통신 완료");

		return userInfo;
	}
}
