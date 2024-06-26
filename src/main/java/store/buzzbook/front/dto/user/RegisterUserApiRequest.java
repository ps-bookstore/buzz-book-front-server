package store.buzzbook.front.dto.user;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record RegisterUserApiRequest(
	String loginId,
	String password,
	String name,
	String contactNumber,
	String email,
	LocalDate birthday
) {

}
