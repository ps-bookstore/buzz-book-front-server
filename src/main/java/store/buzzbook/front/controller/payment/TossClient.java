package store.buzzbook.front.controller.payment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.dto.payment.TossPaymentCancelRequest;

@Slf4j
@Component
public class TossClient implements PaymentApiClient {

	// @Value("${payment.secret-key}")
	private static final String TEST_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
	private static final String TOSS_PAYMENTS_API_URI = "https://api.tosspayments.com/v1/payments/";

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private final JSONParser parser = new JSONParser();
	// 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
	// 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
	private final String encodedKey = "Basic " + Base64.getEncoder().encodeToString((TEST_SECRET_KEY + ":").getBytes());
	@Value("${payment.auth-token}")
	private String authToken;

	@Override
	public String getPayType() {
		return "toss";
	}

	@Override
	public ResponseEntity<JSONObject> confirm(String request) throws Exception {

		String orderId;
		String amount;
		String paymentKey;
		try {
			// 클라이언트에서 받은 JSON 요청 바디입니다.
			JSONObject requestData = (JSONObject)parser.parse(request);
			paymentKey = (String)requestData.get("paymentKey");
			orderId = (String)requestData.get("orderId");
			amount = (String)requestData.get("amount");
		} catch (ParseException e) {
			throw new RuntimeException("토스 결제 요청중 json 파싱 오류", e);
		}
		JSONObject obj = new JSONObject();
		obj.put("orderId", orderId);
		obj.put("amount", amount);
		obj.put("paymentKey", paymentKey);

		// 결제를 승인하면 결제수단에서 금액이 차감돼요.
		URI uri = new URI(TOSS_PAYMENTS_API_URI + "confirm");
		HttpURLConnection connection = (HttpURLConnection)uri.toURL().openConnection();
		connection.setRequestProperty("Authorization", encodedKey);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);

		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

		int code = connection.getResponseCode();
		boolean isSuccess = code == 200;

		InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

		// 결제 성공 및 실패 비즈니스 로직을 구현하세요.
		Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
		JSONObject jsonObject = (JSONObject)parser.parse(reader);
		responseStream.close();

		return ResponseEntity.status(code).body(jsonObject);
	}

	@Override
	public ResponseEntity<JSONObject> read(String paymentKey) {

		HttpClient httpClient = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_1_1)
			.build();

		HttpRequest request = HttpRequest.newBuilder()
			.GET()
			.uri(URI.create(TOSS_PAYMENTS_API_URI + paymentKey))
			.header("Authorization", encodedKey)
			.header("Content-Type", "application/json")
			.build();

		JSONObject jsonObject;
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				log.info("Payment inquiry successful");
			} else {
				log.warn("Real payment inquiry failed. Status code: {}", response.statusCode());
			}

			jsonObject = (JSONObject)parser.parse(response.body());
			return ResponseEntity.status(response.statusCode()).body(jsonObject);

		} catch (IOException e) {
			throw new RuntimeException("결제 조회 IO 예외 발생", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("결제 조회 Interrupted 예외 발생", e);
		} catch (ParseException e) {
			throw new RuntimeException("String -> JSONObject 파싱 예외 발생", e);
		}

	}


	public ResponseEntity<JSONObject> cancel(String paymentKey, TossPaymentCancelRequest cancelReq) {

		HttpClient httpClient = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_1_1)
			.build();

		ObjectNode jsonNodes = objectMapper.createObjectNode();
		jsonNodes.put("cancelReason", cancelReq.getCancelReason());

		if (cancelReq.getCancelAmount() != null) {
			jsonNodes.put("cancelAmount", cancelReq.getCancelAmount());
		}

		String json;
		try {
			json = objectMapper.writeValueAsString(jsonNodes);
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert cancel request to JSON", e);
		}

		HttpRequest request = HttpRequest.newBuilder()
			.POST(HttpRequest.BodyPublishers.ofString(json))
			.uri(URI.create(TOSS_PAYMENTS_API_URI + paymentKey + "/cancel"))
			.header("Authorization", encodedKey)
			.header("Content-Type", "application/json")
			.build();

		HttpResponse<String> response;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			throw new RuntimeException("HTTP request failed", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("HTTP request interrupted", e);
		}

		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) parser.parse(response.body());
		} catch (ParseException e) {
			throw new RuntimeException("Error parsing JSON response", e);
		}

		String errorCode = (String) jsonObject.get("code");
		String errorMessage = (String) jsonObject.get("message");
		if (response.statusCode() == 200) {
			log.info("Payment cancel successful");
		} else {
			log.warn("Payment cancel failed. HttpStatus code: {}\nErrorCode: {}\nErrorMessage: {}\n", response.statusCode(), errorCode, errorMessage);
		}

		return ResponseEntity.status(response.statusCode()).body(jsonObject);
	}

	@Override
	public boolean matchPayType(String payType) {
		return PaymentApiClient.super.matchPayType(payType);
	}

}
