package store.buzzbook.front.dto.user;

import lombok.Builder;

@Builder
public record RegisterUserApiRequest(
	String loginId,
	String password,
	String name,
	String contactNumber,
	String email,
	String birthday
) {

}
