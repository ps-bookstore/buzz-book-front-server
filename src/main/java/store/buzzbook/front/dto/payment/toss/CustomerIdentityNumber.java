package store.buzzbook.front.dto.payment.toss;

// 명세서에 없는 객체

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerIdentityNumber {
	private CustomerMobilePhone customerMobilePhone;
	private String businessRegistrationNumber;
	private String cashReceiptCardNum;
}
