package store.buzzbook.front.dto.payment;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.front.controller.payment.dto.PayInfo;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class CreateCancelBillLogRequest {
	private String orderId;
	private PayInfo payInfo;
	@Nullable
	private String cancelReason;
	private BillStatus status;
}
