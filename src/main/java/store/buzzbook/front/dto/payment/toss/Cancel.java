package store.buzzbook.front.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Cancel {
	private Integer cancelAmount;
	private String cancelReason;
	private Integer taxFreeAmount;
	private Integer taxExemptionAmount;
	private Integer refundableAmount;
	private Integer easyPayDiscountAmount;
	private String canceledAt;
	private String transactionKey; // 취소 건의 키 값입니다. 여러 건의 취소 거래를 구분하는 데 사용됩니다. 최대 길이는 64자입니다.
	private String receiptKey;
	private String cancelStatus;
	private String cancelrequestId;
}
