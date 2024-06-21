package store.buzzbook.front.dto.user;

public record JwtLoginUser (
	String username,
	String password,
	String role
) {

}
