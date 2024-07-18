package store.buzzbook.front.dto.user;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record RegisterUserRequest(
	@NotEmpty(message = "로그인 아이디는 필수사항입니다.")
	@Size(min = 6, max = 20)
	String loginId,
	@NotEmpty(message = "비밀번호는 필수사항입니다.")
	@Size(min = 6)
	String password,
	@NotEmpty(message = "비밀번호 확인은 필수사항입니다.")
	@Size(min = 6)
	String confirmedPassword,
	@NotEmpty(message = "이름은 필수사항입니다.")
	@Size(max = 20)
	String name,
	@NotEmpty(message = "연락처는 필수사항입니다.")
	@Pattern(regexp = "\\d+", message = "숫자만 입력 가능합니다.")
	String contactNumber,
	@NotEmpty(message = "이메일은 필수사항입니다.")
	@Email(message = "이메일 형식을 맞춰주십시오.")
	String email,
	Boolean emailVerified,
	@NotNull(message = "생일은 필수사항입니다.")
	LocalDate birthday
){

	public RegisterUserApiRequest toApiRequest(PasswordEncoder passwordEncoder) {
		return RegisterUserApiRequest.builder()
			.contactNumber(this.contactNumber())
			.email(this.email())
			.name(this.name())
			.birthday(this.birthday())
			.loginId(this.loginId())
			.password(passwordEncoder.encode(this.password()))
			.build();
	}
}
