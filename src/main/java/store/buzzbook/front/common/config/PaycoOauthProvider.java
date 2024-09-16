// package store.buzzbook.front.common.config;
//
// import java.util.Objects;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.client.RestTemplate;
//
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import lombok.extern.slf4j.Slf4j;
// import store.buzzbook.front.common.exception.user.UnknownApiException;
// import store.buzzbook.front.dto.secret.SecretResponse;
//
// @Slf4j
// @Configuration
// @NoArgsConstructor
// @Getter
// @Setter
// public class PaycoOauthProvider {
// 	@Value("${nhncloud.keymanager.appkey}")
// 	private String appKey;
// 	@Value("${nhncloud.keymanager.payco-client-id}")
// 	private String clientIdSecretId;
// 	@Value("${nhncloud.keymanager.payco-secret-key}")
// 	private String secretKeySecretId;
//
// 	@Bean
// 	public PaycoOauthSecrets paycoOauthSecrets() {
// 		String key = "secret";
// 		String secretUrl = "https://api-keymanager.nhncloudservice.com/keymanager/v1.0/appkey/%s/secrets/%s";
//
// 		RestTemplate restTemplate = new RestTemplate();
//
// 		SecretResponse clientIdResponse = restTemplate.getForObject(String.format(secretUrl, appKey, clientIdSecretId), SecretResponse.class);
// 		SecretResponse secretKeyResponse = restTemplate.getForObject(String.format(secretUrl, appKey, secretKeySecretId), SecretResponse.class);
//
// 		if(Objects.isNull(clientIdResponse) || Objects.isNull(secretKeyResponse)) {
// 			log.error("Failed to get secret response");
// 			throw new UnknownApiException("Could not get secret response");
// 		}
//
// 		String clientId = (String)clientIdResponse.getBody().get(key);
// 		String secretKey = (String)secretKeyResponse.getBody().get(key);
//
// 		return new PaycoOauthSecrets(clientId,secretKey);
// 	}
// }
