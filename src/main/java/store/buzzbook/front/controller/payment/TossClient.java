package store.buzzbook.front.controller.payment;

import static org.springframework.http.MediaType.*;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import store.buzzbook.front.common.util.ApiUtils;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.OrderFormData;
import store.buzzbook.front.dto.payment.PaymentCancelRequest;
import store.buzzbook.front.dto.payment.PaymentConfirmationRequest;
import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;
import store.buzzbook.front.dto.payment.TossPaymentCancelRequest;

@Component
public class TossClient implements PaymentApiClient {

	private RestClient restClient;

	@Value("${payment.auth-token}")
	private String authToken;

	@Override
	public String getPayType() {
		return "toss";
	}

	@Override
	public ResponseEntity<ReadPaymentResponse> confirm(@ModelAttribute("orderFormData") OrderFormData request) {
		ResponseEntity<ReadPaymentResponse> paymentResponse = restClient.post()
			.uri(ApiUtils.getTossPaymentBasePath()+"/confirm")
			.header(APPLICATION_JSON_VALUE)
			.header(HttpHeaders.AUTHORIZATION, "Basic " + authToken)
			.body(PaymentConfirmationRequest.builder().paymentKey(UUID.randomUUID().toString()).amount(
				request.getPrice()).orderId(
				request.getOrderStr()).build())
			.retrieve()
			.toEntity(ReadPaymentResponse.class);

		restClient.post()
			.uri(ApiUtils.getPaymentBasePath()+"/bill-log")
			.header(APPLICATION_JSON_VALUE)
			.body(paymentResponse)
			.retrieve()
			.toEntity(ReadBillLogResponse.class);
		return paymentResponse;
	}

	@Override
	public ResponseEntity<ReadPaymentResponse> cancel(@RequestBody PaymentCancelRequest request) {
		return restClient.post()
			.uri(ApiUtils.getTossPaymentBasePath()+"/"+request.getPaymentKey()+"/cancel")
			.header(APPLICATION_JSON_VALUE)
			.header(HttpHeaders.AUTHORIZATION, "Basic " + authToken)
			.body(new TossPaymentCancelRequest(request.getCancelReason(), request.getCancelAmount()))
			.retrieve()
			.toEntity(ReadPaymentResponse.class);
	}

	@Override
	public boolean matchPayType(String payType) {
		return PaymentApiClient.super.matchPayType(payType);
	}
}
