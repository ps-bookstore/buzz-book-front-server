package store.buzzbook.front.controller.payment;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.front.dto.payment.PaymentCancelRequest;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;

public interface PaymentApiClient {
	String getPayType();
	ResponseEntity<JSONObject> confirm(@RequestBody String request) throws Exception;
	ResponseEntity<ReadPaymentResponse> cancel(PaymentCancelRequest request);
	default boolean matchPayType(String payType) {
		return payType.trim().equals(getPayType());
	}
	ResponseEntity<ReadPaymentResponse> read(String paymentKey);
}
