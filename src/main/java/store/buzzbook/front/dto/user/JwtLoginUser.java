package store.buzzbook.front.dto.user;

import lombok.Builder;

@Builder
public record JwtLoginUser (
	String username,
	String password,
	String role
) {

}
