package store.buzzbook.front.controller.payment;

import org.springframework.http.ResponseEntity;

import store.buzzbook.front.dto.order.OrderFormData;
import store.buzzbook.front.dto.payment.PaymentCancelRequest;
import store.buzzbook.front.dto.payment.PaymentConfirmationRequest;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;

public interface PaymentApiClient {
	String getPayType();
	ResponseEntity<ReadPaymentResponse> confirm(PaymentConfirmationRequest request);
	ResponseEntity<ReadPaymentResponse> cancel(PaymentCancelRequest request);
	default boolean matchPayType(String payType) {
		return payType.trim().equals(getPayType());
	}
	ResponseEntity<ReadPaymentResponse> read(String paymentKey);
}
