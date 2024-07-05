package store.buzzbook.front.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record DeactivateUserRequest(
	@NotEmpty(message = "비밀번호는 필수사항입니다.")
	@Size(min = 6)
	String password,
	@NotEmpty
	@Size(min = 6)
	String reason
) {
}
