package store.buzzbook.front.dto.user;


public record ActivateRequest(String token, String code) {
}
