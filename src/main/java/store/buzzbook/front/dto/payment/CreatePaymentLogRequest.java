package store.buzzbook.front.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreatePaymentLogRequest {
	private long billLogId;
	@Setter
	private String name;
	private int amount;
	@Setter
	private String loginId;
	private String orderId;
}
