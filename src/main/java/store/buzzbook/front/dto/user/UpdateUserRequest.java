package store.buzzbook.front.dto.user;

public record UpdateUserRequest(
	String name,
	String contactNumber,
	String email
) {
}
