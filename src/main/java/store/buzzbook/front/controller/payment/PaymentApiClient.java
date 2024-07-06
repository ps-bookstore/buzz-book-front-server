package store.buzzbook.front.controller.payment;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

import store.buzzbook.front.dto.payment.TossPaymentCancelRequest;

public interface PaymentApiClient {
	String getPayType();
	default boolean matchPayType(String payType) {
		return payType.trim().equals(getPayType());
	}

	ResponseEntity<JSONObject> confirm(String request) throws Exception;

	ResponseEntity<JSONObject> cancel(String paymentKey, TossPaymentCancelRequest tossPaymentCancelRequest);

	ResponseEntity<JSONObject> read(String paymentKey);

}
