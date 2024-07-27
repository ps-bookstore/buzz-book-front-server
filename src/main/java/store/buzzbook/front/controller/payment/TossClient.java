package store.buzzbook.front.controller.payment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Supplier;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.order.CoreServerException;
import store.buzzbook.front.common.exception.order.JSONParsingException;
import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.TossErrorResponse;
import store.buzzbook.front.dto.payment.TossPaymentCancelRequest;

/**
 * Toss Client
 *
 * @author 박설, 임용범
 */

@Slf4j
@Component
public class TossClient implements PaymentApiClient {
	private static final int MAX_RETRY_COUNT = 5;
	private static final int RETRY_DELAY_MS = 2000;

	@Value("${api.gateway.host}")
	private String host;
	@Value("${api.gateway.port}")
	private int port;

	// @Value("${payment.secret-key}")
	private static final String TEST_SECRET_KEY = "test_sk_AQ92ymxN349nQDpp12DOVajRKXvd";
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
			throw new JSONParsingException("토스 결제 요청중 json 파싱 오류");
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

		if (isSuccess) {
			sendPaymentInfoToOrderService(jsonObject);
		} else {
			// 결제 실패 시 적절한 오류 처리
			TossErrorResponse tossErrorResponse = objectMapper.convertValue(jsonObject,
				TossErrorResponse.class);

			log.warn(tossErrorResponse.getMessage());

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			HttpEntity<String> paymentKeyEntity = new HttpEntity<>(paymentKey, headers);

			performExchange(() -> restTemplate.exchange(
				String.format("http://%s:%d/api/payments/bill-log/rollback", host, port),
				HttpMethod.POST,
				paymentKeyEntity,
				String.class
				)
			);

			throw new CoreServerException("결제 승인 실패");
		}

		return ResponseEntity.status(code).body(jsonObject);
	}

	@Retryable(
		retryFor = { Exception.class },
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000)
	)
	public <T> void performExchange(Supplier<ResponseEntity<T>> requestSupplier) {
		requestSupplier.get();
	}

	private void sendPaymentInfoToOrderService(JSONObject paymentInfo) throws InterruptedException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<JSONObject> paymentResponse = new HttpEntity<>(paymentInfo, headers);

		try {
			ResponseEntity<ReadBillLogResponse> responseResponseEntity = restTemplate.exchange(
				String.format("http://%s:%d/api/payments/bill-log", host, port),
				HttpMethod.POST,
				paymentResponse,
				ReadBillLogResponse.class
			);
		} catch (Exception e) {
			// 주문 서버로의 결제 정보 전달 실패 시 재시도 로직 구현
			retrySendPaymentInfoToOrderService(paymentInfo);
		}
	}

	private void retrySendPaymentInfoToOrderService(JSONObject paymentInfo) throws InterruptedException {
		// 재시도 로직 구현
		for (int i = 0; i < MAX_RETRY_COUNT; i++) {
			try {
				sendPaymentInfoToOrderService(paymentInfo);
				return; // 성공 시 메서드 종료
			} catch (Exception e) {
				// 재시도 실패 시 로그 남기기
				log.error("주문 서버로의 결제 정보 전달 재시도 실패: " + e.getMessage());
				Thread.sleep(RETRY_DELAY_MS); // 재시도 간격 대기
			}
		}
		throw new CoreServerException("주문 서버로의 결제 정보 전달 실패");
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
			throw new JSONParsingException("Failed to convert cancel request to JSON");
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
			jsonObject = (JSONObject)parser.parse(response.body());
		} catch (ParseException e) {
			throw new JSONParsingException("Error parsing JSON response");
		}

		String errorCode = (String)jsonObject.get("code");
		String errorMessage = (String)jsonObject.get("message");
		if (response.statusCode() == 200) {
			log.info("Payment cancel successful");
		} else {
			log.error("Payment cancel failed. HttpStatus code: {}\nErrorCode: {}\nErrorMessage: {}\n",
				response.statusCode(), errorCode, errorMessage);
		}

		return ResponseEntity.status(response.statusCode()).body(jsonObject);
	}

	@Override
	public boolean matchPayType(String payType) {
		return PaymentApiClient.super.matchPayType(payType);
	}

}
