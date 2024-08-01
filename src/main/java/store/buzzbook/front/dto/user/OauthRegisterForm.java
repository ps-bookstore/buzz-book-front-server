package store.buzzbook.front.dto.user;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public class OauthRegisterForm {
	@NotEmpty(message = "이메일은 필수사항입니다.")
	@Email(message = "이메일 형식을 맞춰주십시오.")
	private String email;
	@NotNull(message = "생일은 필수사항입니다.")
	private LocalDate birthday;
	@NotEmpty(message = "연락처는 필수사항입니다.")
	@Pattern(regexp = "\\d+", message = "숫자만 입력 가능합니다.")
	private String contactNumber;
	@NotEmpty(message = "이름은 필수사항입니다.")
	@Size(max = 20)
	private String name;
	@NotEmpty
	private String provider;


	public OauthRegisterRequest toOauthRegisterRequest() {
		return new OauthRegisterRequest(email, birthday, contactNumber, name, provider);
	}
}
