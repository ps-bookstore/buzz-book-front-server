package store.buzzbook.front.dto.user;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record UserInfo(Long id, String loginId,
					   String contactNumber, String name, String email,
					   LocalDate birthday, Grade grade, boolean isAdmin) {
}
