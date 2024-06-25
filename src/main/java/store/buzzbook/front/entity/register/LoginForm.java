package store.buzzbook.front.entity.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class LoginForm {
	private String id;
	private String password;
}
