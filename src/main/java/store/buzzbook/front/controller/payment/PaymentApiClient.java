package store.buzzbook.front.controller.payment;

import java.io.IOException;
import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

public interface PaymentApiClient {
	String getPayType();
	ResponseEntity<JSONObject> confirm(String request) throws Exception;
	HttpResponse<String> cancel(String paymentKey, String cancelReason) throws IOException, InterruptedException;
	default boolean matchPayType(String payType) {
		return payType.trim().equals(getPayType());
	}
	ResponseEntity<JSONObject> read(String paymentKey);
}
