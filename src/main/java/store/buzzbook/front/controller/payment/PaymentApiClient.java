package store.buzzbook.front.controller.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.front.dto.order.OrderFormData;
import store.buzzbook.front.dto.payment.PaymentCancelRequest;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;

public interface PaymentApiClient {
	String getPayType();
	ResponseEntity<ReadPaymentResponse> confirm(@ModelAttribute("orderFormData") OrderFormData request);
	ResponseEntity<ReadPaymentResponse> cancel(@RequestBody PaymentCancelRequest request);
	default boolean matchPayType(String payType) {
		return payType.trim().equals(getPayType());
	}
}
