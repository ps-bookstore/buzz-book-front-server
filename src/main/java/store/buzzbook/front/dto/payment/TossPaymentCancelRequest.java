package store.buzzbook.front.dto.payment;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
public class TossPaymentCancelRequest {
	private String cancelReason;
	@Nullable
	private Integer cancelAmount;
}
