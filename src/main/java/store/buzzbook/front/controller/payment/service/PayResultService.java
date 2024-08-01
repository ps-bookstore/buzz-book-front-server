package store.buzzbook.front.controller.payment.service;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.order.CoreServerException;
import store.buzzbook.front.controller.payment.adaptor.PayInfoAdaptor;
import store.buzzbook.front.controller.payment.adaptor.PayInfoAdaptorResolver;
import store.buzzbook.front.controller.payment.adaptor.PointPayInfoAdaptor;
import store.buzzbook.front.controller.payment.adaptor.TossPayInfoAdaptor;
import store.buzzbook.front.controller.payment.dto.PayInfo;
import store.buzzbook.front.controller.payment.dto.PayResult;
import store.buzzbook.front.controller.payment.dto.PointPayInfo;
import store.buzzbook.front.controller.payment.dto.SimplePayInfo;

@Service
@Slf4j
public class PayResultService {
	@Value("${api.gateway.host}")
	private String host;
	@Value("${api.gateway.port}")
	private int port;

	private static final int MAX_RETRY_COUNT = 5;
	private static final int RETRY_DELAY_MS = 2000;

	private static PayInfoAdaptorResolver payInfoAdaptorResolver;
	// private static TossPaymentResponse tossPaymentResponse;
	// private static PointPaymentResponse pointPaymentResponse;

	private static final String SIMPLE = "간편결제";
	private static final String POINT = "POINT";

	static {
		// tossPaymentResponse = new TossPaymentResponse();
		// pointPaymentResponse = new PointPaymentResponse();
		payInfoAdaptorResolver = new PayInfoAdaptorResolver(List.of(new PointPayInfoAdaptor(), new TossPayInfoAdaptor()));
	}

	PayInfoAdaptor resolver(PayInfo.PayType payType){
		return payInfoAdaptorResolver.getPayInfoAdaptor(payType);
	}

	public void tossOrder(String orderId, PayResult payResult) {
		SimplePayInfo simplePayInfo = null;
		//payInfo
		if(payResult.getMethod().equals(SIMPLE)){
			simplePayInfo = (SimplePayInfo)resolver(PayInfo.PayType.fromValue(SIMPLE)).convert(payResult);
		}

		//payresult -> api
		sendPaymentInfoToOrderService(simplePayInfo);
	}

	@Retryable(
		retryFor = { CoreServerException.class },
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000)
	)
	private void sendPaymentInfoToOrderService(SimplePayInfo paymentInfo) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<SimplePayInfo> paymentResponse = new HttpEntity<>(paymentInfo, headers);

		try {
			ResponseEntity<String> responseResponseEntity = restTemplate.exchange(
				String.format("http://%s:%d/api/payments/order", host, port),
				HttpMethod.POST,
				paymentResponse,
				String.class
			);
		} catch (Exception e) {
			log.error("주문 서버로의 결제 정보 전달 재시도 실패: " + e.getMessage());
			throw new CoreServerException(e.getMessage());
		}
	}

	public void tossCancel(PayResult payResult) {
		SimplePayInfo simplePayInfo = null;
		//payInfo
		if(payResult.getMethod().equals(SIMPLE)){
			simplePayInfo = (SimplePayInfo)resolver(PayInfo.PayType.fromValue(SIMPLE)).convert(payResult);
		}

		//payresult -> api
		sendPaymentInfoToOrderCancelService(simplePayInfo);
	}

	@Retryable(
		retryFor = { CoreServerException.class },
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000)
	)
	private void sendPaymentInfoToOrderCancelService(SimplePayInfo paymentInfo) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<SimplePayInfo> paymentResponse = new HttpEntity<>(paymentInfo, headers);

		try {
			ResponseEntity<String> responseResponseEntity = restTemplate.exchange(
				String.format("http://%s:%d/api/payments/cancel", host, port),
				HttpMethod.POST,
				paymentResponse,
				String.class
			);
		} catch (Exception e) {
			log.error("주문 서버로의 결제 취소 정보 전달 재시도 실패: " + e.getMessage());
			throw new CoreServerException(e.getMessage());
		}
	}

	public void pointOrder(String orderId, PayResult payResult) {
		PointPayInfo pointPayInfo = null;

		if(payResult.getMethod().equals(POINT)){
			pointPayInfo = (PointPayInfo)resolver(PayInfo.PayType.fromValue(POINT)).convert(payResult);
		}

		sendPaymentInfoToPointOrderService(pointPayInfo);
	}

	@Retryable(
		retryFor = { CoreServerException.class },
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000)
	)
	private void sendPaymentInfoToPointOrderService(PointPayInfo paymentInfo) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<PointPayInfo> paymentResponse = new HttpEntity<>(paymentInfo, headers);

		try {
			ResponseEntity<String> responseResponseEntity = restTemplate.exchange(
				String.format("http://%s:%d/api/payments/order", host, port),
				HttpMethod.POST,
				paymentResponse,
				String.class
			);
		} catch (Exception e) {
			log.error("주문 서버로의 결제 취소 정보 전달 재시도 실패: " + e.getMessage());
			throw new CoreServerException(e.getMessage());
		}
	}

	// public void pointCancel(String orderId, PayResult payResult) {
	// 	PointPayInfo pointPayInfo = null;
	// 	//payInfo
	// 	if(payResult.getMethod().equals(POINT)){
	// 		pointPayInfo = (PointPayInfo)resolver(PayInfo.PayType.fromValue(POINT)).convert(payResult);
	// 	}
	//
	// 	//payresult -> api
	// 	sendPaymentInfoToPointCancelService(pointPayInfo);
	// }
	//
	// @Retryable(
	// 	retryFor = { CoreServerException.class },
	// 	maxAttempts = 3,
	// 	backoff = @Backoff(delay = 2000)
	// )
	// private void sendPaymentInfoToPointCancelService(PointPayInfo paymentInfo) {
	// 	RestTemplate restTemplate = new RestTemplate();
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.set("Content-Type", "application/json");
	// 	HttpEntity<PointPayInfo> paymentResponse = new HttpEntity<>(paymentInfo, headers);
	//
	// 	try {
	// 		ResponseEntity<String> responseResponseEntity = restTemplate.exchange(
	// 			String.format("http://%s:%d/api/payments/cancel", host, port),
	// 			HttpMethod.POST,
	// 			paymentResponse,
	// 			String.class
	// 		);
	// 	} catch (Exception e) {
	// 		log.error("주문 서버로의 결제 취소 정보 전달 재시도 실패: " + e.getMessage());
	// 		throw new CoreServerException(e.getMessage());
	// 	}
	// }
}

