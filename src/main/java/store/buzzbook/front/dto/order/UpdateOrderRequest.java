package store.buzzbook.front.dto.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class UpdateOrderRequest {
	private long id;
	private String loginId;
	private String orderStatusName;
}
