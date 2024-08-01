package store.buzzbook.front.dto.secret;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class SecretResponse {
	@JsonProperty("header")
	private Map<String,Object> header = new HashMap<>();

	@JsonProperty("body")
	private Map<String,Object> body = new HashMap<>();
}
