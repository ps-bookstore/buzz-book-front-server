package store.buzzbook.front.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;



@AllArgsConstructor
@Getter
public class PaycoOauthSecrets {
	private String clientId;
	private String clientSecret;
}
