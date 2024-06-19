package store.buzzbook.front.dto.user;

import lombok.Builder;

@Builder
public record LoginUserResponse(String loginId, String password) {

}
