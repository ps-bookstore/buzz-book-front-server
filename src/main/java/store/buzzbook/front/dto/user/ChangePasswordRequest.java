package store.buzzbook.front.dto.user;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ChangePasswordRequest{
	@Size(min = 6)
	private String oldPassword;
	@Size(min = 6)
	private String newPassword;
	@Size(min = 6)
	private String confirmPassword;

	public void encryptPassword(PasswordEncoder passwordEncoder) {
		this.newPassword = passwordEncoder.encode(newPassword);
	}

	public boolean isConfirmed(){
		return newPassword.equals(confirmPassword) && !oldPassword.equals(newPassword);
	}
}
