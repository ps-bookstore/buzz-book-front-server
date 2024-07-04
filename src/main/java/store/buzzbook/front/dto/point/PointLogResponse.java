package store.buzzbook.front.dto.point;

import java.time.LocalDateTime;

public record PointLogResponse(
	LocalDateTime createdAt,
	String inquiry,
	int delta,
	int balance
) {
}
