package store.buzzbook.front.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UpdateOrderDetailRequest {
	private long id;
	private String orderStatusName;
	private String loginId;
}
