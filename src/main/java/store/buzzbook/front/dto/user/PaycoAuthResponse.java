package store.buzzbook.front.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class PaycoAuthResponse {
	@JsonProperty("access_token_secret")
	private String accessTokenSecret;
	@JsonProperty("state")
	private String state;
	@JsonProperty("token_type")
	private String tokenType;
	@JsonProperty("expires_in")
	private String expiresIn;
	@JsonProperty("refresh_token")
	private String refreshToken;
	@JsonProperty("access_token")
	private String accessToken;
}
