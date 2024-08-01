package store.buzzbook.front.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReadOrderIdByPaymentKeyRequest {
	private String paymentKey;
}
