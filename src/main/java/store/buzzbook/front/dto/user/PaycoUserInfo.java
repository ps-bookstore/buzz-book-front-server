package store.buzzbook.front.dto.user;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaycoUserInfo {
	@JsonProperty("header")
	private Map<String,Object> header;

	@JsonProperty("data")
	private Map<String,Map<String,Object>> data;


	public String getIdNo(){
		return (String) getMember().get("idNo");
	}
	public String getEmail(){
		return (String) getMember().get("email");
	}
	public String getBirthday(){
		return (String) getMember().get("birthdayMMdd");
	}
	public String getMobile(){
		return (String) getMember().get("mobile");
	}
	public String getName(){
		return (String) getMember().get("name");
	}

	public Map<String,Object> getMember(){
		return data.get("member");
	}
}
