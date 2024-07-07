package store.buzzbook.front.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CreateBillLogRequest {
	private String payment;
	private int price;
	private String paymentKey;
	private String orderId;
	private String cancelReason;
}
