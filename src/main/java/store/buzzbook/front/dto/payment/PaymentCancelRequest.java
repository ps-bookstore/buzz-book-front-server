package store.buzzbook.front.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.front.dto.user.UserInfo;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCancelRequest {
	private String paymentKey;
	private String cancelReason;
	// private UserInfo userInfo;
	private Integer cancelAmount; // 취소할 금액입니다. 값이 없으면 전액 취소됩니다.
}
