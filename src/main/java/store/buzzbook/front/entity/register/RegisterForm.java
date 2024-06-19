package store.buzzbook.front.entity.register;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RegisterForm {
	private String id;
	private String name;
	private String email;
	private String password;
	private String confirmPassword;
	private String tel;
	private String birth;
}
