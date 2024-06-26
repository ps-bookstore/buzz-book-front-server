package store.buzzbook.front.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TossPaymentCancelRequest {
	private String cancelReason;
	private Integer cancelAmount;
}
