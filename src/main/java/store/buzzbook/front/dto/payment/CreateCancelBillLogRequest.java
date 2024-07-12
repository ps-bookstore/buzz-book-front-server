package store.buzzbook.front.dto.payment;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class CreateCancelBillLogRequest {
	private String paymentKey;
	@Nullable
	private String cancelReason;
	private BillStatus status;
	@Nullable
	private String payment;
}
