package store.buzzbook.front.dto.user;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class PaycoUserData{
	@JsonProperty("member")
	private Map<String,Object> member;
}
