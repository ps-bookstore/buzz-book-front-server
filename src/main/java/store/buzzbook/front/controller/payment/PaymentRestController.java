package store.buzzbook.front.controller.payment;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.front.dto.payment.CreatePaymentLogRequest;
import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.ReadPaymentLogResponse;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;

@RestController
public class PaymentRestController {
	@Value("${api.core.host}")
	private String host;

	@Value("${api.core.port}")
	private int port;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@PostMapping("/bill-log/register")
	public ResponseEntity<ReadBillLogResponse> createBillLog(@RequestBody ReadPaymentResponse response) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<ReadPaymentResponse> paymentResponse = new HttpEntity<>(response, headers);

		ResponseEntity<ReadBillLogResponse> responseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-log", host, port), HttpMethod.POST, paymentResponse, ReadBillLogResponse.class);

		return responseResponseEntity;
	}

	@PostMapping("/payment-log/register")
	public ResponseEntity<ReadPaymentLogResponse> createPaymentLog(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		String orderId;
		String amount;
		String billLogId;
		String method;

		try {
			JSONObject requestData = (JSONObject) parser.parse(request);
			billLogId = (String) requestData.get("billLogId");
			orderId = (String) requestData.get("orderId");
			amount = (String) requestData.get("amount");
			method = (String) requestData.get("method");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		JSONObject obj = new JSONObject();
		obj.put("orderId", orderId);
		obj.put("amount", amount);
		obj.put("billLogId", billLogId);
		obj.put("name", method);

		CreatePaymentLogRequest createPaymentLogRequest = objectMapper.convertValue(obj, CreatePaymentLogRequest.class);

		createPaymentLogRequest.setLoginId("parkseol");

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<CreatePaymentLogRequest> paymentLogRequest = new HttpEntity<>(createPaymentLogRequest, headers);

		ResponseEntity<ReadPaymentLogResponse> responseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/payment-log", host, port), HttpMethod.POST, paymentLogRequest, ReadPaymentLogResponse.class);

		return responseResponseEntity;
	}
}
