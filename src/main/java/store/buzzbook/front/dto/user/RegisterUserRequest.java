package store.buzzbook.front.dto.user;

import java.time.ZonedDateTime;

public record RegisterUserRequest(
	String loginId,
	String password,
	String confirmedPassword,
	String name,
	String contactNumber,
	String email,
	String birthday
){

	@Override
	public String toString() {
		return "RegisterUserRequest{" +
			"loginId='" + loginId + '\'' +
			", password='" + password + '\'' +
			", confirmedPassword='" + confirmedPassword + '\'' +
			", name='" + name + '\'' +
			", contactNumber='" + contactNumber + '\'' +
			", email='" + email + '\'' +
			", birthday=" + birthday +
			'}';
	}
}
