package store.buzzbook.front.dto.user;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.front.service.user.UserAuthService;

@NoArgsConstructor
@AllArgsConstructor
public class PaycoLogoutResponse {
	@JsonProperty("rtn_data")
	private Map<String, Object> data;

	@Getter
	@JsonProperty("rtn_msg")
	private String msg;

	@Getter
	@JsonProperty("rtn_cd")
	private Integer cd;


	public Integer getLoginStatus(){
		return (Integer)data.getOrDefault(UserAuthService.PAYCO_LOGIN_STATUS, 1);
	}
}
