package store.buzzbook.front.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EasyPay {
	private String provider;
	private Integer amount;
	private Integer discountAmount;
}
