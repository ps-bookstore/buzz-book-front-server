package store.buzzbook.front.controller.payment;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import store.buzzbook.front.dto.payment.PaymentCancelRequest;
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
	public ResponseEntity<JSONObject> confirm(@RequestBody String request) throws Exception {

		JSONParser parser = new JSONParser();
		String orderId;
		String amount;
		String paymentKey;
		try {
			// 클라이언트에서 받은 JSON 요청 바디입니다.
			JSONObject requestData = (JSONObject) parser.parse(request);
			paymentKey = (String) requestData.get("paymentKey");
			orderId = (String) requestData.get("orderId");
			amount = (String) requestData.get("amount");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		};
		JSONObject obj = new JSONObject();
		obj.put("orderId", orderId);
		obj.put("amount", amount);
		obj.put("paymentKey", paymentKey);

		// 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
		// 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
		String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
		String authorizations = "Basic " + new String(encodedBytes);

		// 결제를 승인하면 결제수단에서 금액이 차감돼요.
		URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", authorizations);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);

		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(obj.toString().getBytes("UTF-8"));

		int code = connection.getResponseCode();
		boolean isSuccess = code == 200;

		InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

		// 결제 성공 및 실패 비즈니스 로직을 구현하세요.
		Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
		JSONObject jsonObject = (JSONObject) parser.parse(reader);
		responseStream.close();

		return ResponseEntity.status(code).body(jsonObject);
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
		headers.set("Authorization", "Basic " + authToken);

		return ResponseEntity.ok(restTemplate.getForObject("https://api.tosspayments.com/v1/payments/" + paymentKey,
			ReadPaymentResponse.class));
	}
}
