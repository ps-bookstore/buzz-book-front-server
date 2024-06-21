package store.buzzbook.front.dto.user;

public record RegisterUserResponse(
        int status,
        String name,
        String loginId,
        String message
) {
}
