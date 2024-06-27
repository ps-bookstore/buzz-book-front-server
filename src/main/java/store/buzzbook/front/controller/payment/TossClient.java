package store.buzzbook.front.controller.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import store.buzzbook.front.dto.order.OrderFormData;
import store.buzzbook.front.dto.payment.PaymentCancelRequest;
import store.buzzbook.front.dto.payment.PaymentConfirmationRequest;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;

@Component
public class TossClient implements PaymentApiClient {

	@Value("${payment.auth-token}")
	private String authToken;

	@Override
	public String getPayType() {
		return "toss";
	}

	@Override
	public ResponseEntity<ReadPaymentResponse> confirm(@ModelAttribute("paymentConfirmationRequest") PaymentConfirmationRequest request) {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<PaymentConfirmationRequest> confirmRequest = new HttpEntity<>(request, headers);

		return restTemplate.exchange(
			"https://api.tosspayments.com/v1/payments/confirm", HttpMethod.POST, confirmRequest, ReadPaymentResponse.class);
	}

	@Override
	public ResponseEntity<ReadPaymentResponse> cancel(@RequestBody PaymentCancelRequest request) {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Authorization", "Bearer " + authToken);

		HttpEntity<PaymentCancelRequest> cancelRequest = new HttpEntity<>(request, headers);

		return restTemplate.exchange(
			"https://api.tosspayments.com/v1/payments/"+request.getPaymentKey()+"/cancel", HttpMethod.POST, cancelRequest, ReadPaymentResponse.class);

	}

	@Override
	public boolean matchPayType(String payType) {
		return PaymentApiClient.super.matchPayType(payType);
	}

	@Override
	public ResponseEntity<ReadPaymentResponse> read(String paymentKey) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Authorization", "Bearer " + authToken);

		return ResponseEntity.ok(restTemplate.getForObject("https://api.tosspayments.com/v1/payments/" + paymentKey,
			ReadPaymentResponse.class));
	}
}
