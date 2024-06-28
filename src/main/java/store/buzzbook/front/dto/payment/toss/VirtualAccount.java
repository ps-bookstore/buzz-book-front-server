package store.buzzbook.front.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VirtualAccount {
	private String accountType;
	private String accountNumber;
	private String bankCode;
	private String customerName;
	private String dueDate;
	private String refundStatus;
	private Boolean expired;
	private RefundReceiveAccount refundReceiveAccount;
}
