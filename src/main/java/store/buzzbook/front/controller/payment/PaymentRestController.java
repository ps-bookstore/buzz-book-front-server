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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.front.common.exception.order.JSONParsingException;
import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;

@RestController
public class PaymentRestController {

	@Value("${api.gateway.host}")
	private String host;
	@Value("${api.gateway.port}")
	private int port;

	private final ObjectMapper objectMapper = new ObjectMapper();

	// @PostMapping("/bill-log/register")
	// public ResponseEntity<ReadBillLogResponse> createBillLog(@RequestBody ReadPaymentResponse response) {
	//
	// 	RestTemplate restTemplate = new RestTemplate();
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.set("Content-Type", "application/json");
	//
	// 	String jsonString = null;
	// 	try {
	// 		jsonString = objectMapper.writeValueAsString(response);
	// 	} catch (JsonProcessingException e) {
	// 		throw new JSONParsingException(e.getMessage());
	// 	}
	//
	// 	HttpEntity<String> paymentResponse = new HttpEntity<>(jsonString, headers);
	//
	// 	ResponseEntity<ReadBillLogResponse> responseResponseEntity = restTemplate.exchange(
	// 		String.format("http://%s:%d/api/payments/bill-log", host, port), HttpMethod.POST, paymentResponse,
	// 		ReadBillLogResponse.class);
	//
	// 	return responseResponseEntity;
	// }
}
