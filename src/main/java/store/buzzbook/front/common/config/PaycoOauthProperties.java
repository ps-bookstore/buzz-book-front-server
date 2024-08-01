package store.buzzbook.front.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.payco.registration")
public class PaycoOauthProperties {
	private String scope;
	private String codeGrantType;
	private String authorizationGrantType;
	private String refreshGrantType;
	private String redirectUri;
	private String tokenUri;
	private String userInfoUri;
	private String authorizationUri;
	private String userNameAttribute;
}
