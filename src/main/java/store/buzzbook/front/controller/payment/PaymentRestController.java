package store.buzzbook.front.controller.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;

@RestController
public class PaymentRestController {
	@Value("${api.gateway.host}")
	private String host;

	@Value("${api.gateway.port}")
	private int port;

	@PostMapping("/bill-log/register")
	public ResponseEntity<ReadBillLogResponse> createBillLog(@RequestBody ReadPaymentResponse response) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<ReadPaymentResponse> paymentResponse = new HttpEntity<>(response, headers);

		ResponseEntity<ReadBillLogResponse> responseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-log", host, port), HttpMethod.POST, paymentResponse,
			ReadBillLogResponse.class);

		return responseResponseEntity;
	}
}
