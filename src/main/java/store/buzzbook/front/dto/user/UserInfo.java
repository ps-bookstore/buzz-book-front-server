package store.buzzbook.front.dto.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfo implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@NotNull
	private Long id;
	@NotEmpty(message = "로그인 아이디는 필수사항입니다.")
	@Size(min = 6, max = 20)
	private String loginId;

	@NotEmpty(message = "비밀번호는 필수사항입니다.")
	@Size(min = 6)
	private String password;

	@NotEmpty(message = "비밀번호 확인은 필수사항입니다.")
	@Size(min = 6)
	private String confirmedPassword;

	@NotEmpty(message = "이름은 필수사항입니다.")
	@Size(max = 20)
	private String name;

	@NotEmpty(message = "연락처는 필수사항입니다.")
	@Pattern(regexp = "\\d+", message = "숫자만 입력 가능합니다.")
	private String contactNumber;

	@NotEmpty(message = "이메일은 필수사항입니다.")
	@Email(message = "이메일 형식을 맞춰주십시오.")
	private String email;

	@NotNull(message = "생일은 필수사항입니다.")
	private LocalDate birthday;

	private Grade grade;

	private boolean isAdmin;
}
