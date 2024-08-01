package store.buzzbook.front.controller.payment.dto;

import lombok.Getter;

@Getter
public class PointPaymentResponse extends PayResult {

	public PointPaymentResponse(int totalAmount, String paymentKey, String orderId) {
		super("POINT", totalAmount, paymentKey, orderId);
	}
}
