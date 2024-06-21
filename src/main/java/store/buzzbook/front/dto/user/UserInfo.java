package store.buzzbook.front.dto.user;

import java.time.ZonedDateTime;

import lombok.Builder;
import store.buzzbook.front.entity.user.Grade;

@Builder
public record UserInfo(String loginId,
					   String contactNumber, String name, String email,
					   ZonedDateTime birthday, Grade grade, boolean isAdmin) {
}
