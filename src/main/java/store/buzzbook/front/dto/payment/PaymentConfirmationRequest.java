package store.buzzbook.front.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmationRequest {
	private String paymentKey;
	private String orderId;
	private Integer amount;
}
