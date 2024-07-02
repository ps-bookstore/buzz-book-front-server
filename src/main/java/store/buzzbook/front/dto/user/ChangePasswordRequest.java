package store.buzzbook.front.dto.user;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ChangePasswordRequest{
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;

	public void encryptPassword(PasswordEncoder passwordEncoder) {
		this.newPassword = passwordEncoder.encode(newPassword);
	}

	public boolean isConfirmed(){
		return newPassword.equals(confirmPassword) && !oldPassword.equals(newPassword);
	}
}
