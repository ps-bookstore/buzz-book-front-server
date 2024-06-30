package store.buzzbook.front.dto.user;

public record DeactivateUserRequest(
	String password,
	String reason
) {
}
