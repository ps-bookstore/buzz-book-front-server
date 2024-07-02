package store.buzzbook.front.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ReadOrderRequest {
	private String orderId;
	private String loginId;
}
