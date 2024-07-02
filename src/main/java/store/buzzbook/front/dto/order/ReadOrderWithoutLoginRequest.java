package store.buzzbook.front.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReadOrderWithoutLoginRequest {
	private String orderId;
	private String orderPassword;
}
