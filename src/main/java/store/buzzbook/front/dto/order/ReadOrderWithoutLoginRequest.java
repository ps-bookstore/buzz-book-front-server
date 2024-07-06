package store.buzzbook.front.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ReadOrderWithoutLoginRequest {
	private String orderId;
	private String orderEmail;
}
