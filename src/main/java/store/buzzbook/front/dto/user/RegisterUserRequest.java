package store.buzzbook.front.dto.user;

import java.time.ZonedDateTime;

import lombok.Builder;

public record RegisterUserRequest(
	String loginId,
	String password,
	String confirmedPassword,
	String name,
	String contactNumber,
	String email,
	String birthday
){

}
