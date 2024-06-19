package store.buzzbook.front.entity.register;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginForm {
	private String id;
	private String password;
}
