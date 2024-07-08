package store.buzzbook.front.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class LoginForm {
	@NotEmpty(message = "아이디 입력은 필수사항입니다.")
	@Size(min = 6, max = 20, message = "아이디는 6자 이상, 20자 이상 입력해주세요.")
	private String id;
	@NotEmpty(message = "비밀번호 입력은 필수사항입니다.")
	@Size(min = 6, message = "비밀번호는 6자 이상 입력해주세요.")
	private String password;
}
