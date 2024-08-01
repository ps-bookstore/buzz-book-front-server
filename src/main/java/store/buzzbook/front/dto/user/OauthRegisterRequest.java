package store.buzzbook.front.dto.user;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
public class OauthRegisterRequest {
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


	public OauthRegisterRequest(String email, LocalDate birthday, String contactNumber, String name, String provider) {
		this.email = email;
		this.birthday = birthday;
		this.contactNumber = contactNumber;
		this.name = name;
		this.provider = provider;
	}

	@Setter
	private String provideId;
	private String loginId;
	private String password;


	public void generateLoginIdAndPassword(PasswordEncoder passwordEncoder) {
		loginId = String.format("%s_%s",provider, provideId);
		password = String.format("OO%s%sAA", provideId, provider);
		password = passwordEncoder.encode(password);
	}
}
