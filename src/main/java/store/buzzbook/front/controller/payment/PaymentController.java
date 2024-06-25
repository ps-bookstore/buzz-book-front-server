package store.buzzbook.front.controller.payment;

import static org.springframework.http.MediaType.*;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.front.common.util.ApiUtils;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.payment.PaymentCancelRequest;
import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.PaymentConfirmationRequest;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;
import store.buzzbook.front.dto.payment.TossPaymentCancelRequest;

@Controller
public class PaymentController {

	private RestClient restClient;

	@Value("${payment.auth-token}")
	private String authToken;

	@PostMapping("/confirm")
	public ResponseEntity<ReadPaymentResponse> confirmPaymentRestClient(@ModelAttribute("createOrderRequest") CreateOrderRequest request) {

		ResponseEntity<ReadOrderResponse> readOrderResponse = restClient.post()
			.uri(ApiUtils.getOrderBasePath())
			.header(APPLICATION_JSON_VALUE)
			.body(request)
			.retrieve()
			.toEntity(ReadOrderResponse.class);

		ResponseEntity<ReadPaymentResponse> paymentResponse = restClient.post()
			.uri(ApiUtils.getTossPaymentBasePath()+"/confirm")
			.header(APPLICATION_JSON_VALUE)
			.header(HttpHeaders.AUTHORIZATION, "Basic " + authToken)
			.body(PaymentConfirmationRequest.builder().paymentKey(UUID.randomUUID().toString()).amount(
				Objects.requireNonNull(readOrderResponse.getBody()).getPrice()).orderId(
				String.valueOf(readOrderResponse.getBody().getId())).build())
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

	/**
	 * 인증성공처리
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/success")
	public String paymentRequest(HttpServletRequest request, Model model) throws Exception {
		model.addAttribute("page", "success");
		return "index";
	}

	/**
	 * 인증실패처리
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/fail")
	public String failPayment(HttpServletRequest request, Model model) throws Exception {
		String failCode = request.getParameter("code");
		String failMessage = request.getParameter("message");

		model.addAttribute("code", failCode);
		model.addAttribute("message", failMessage);
		model.addAttribute("page", "fail");

		return "index";
	}

	@PostMapping("/cancel2")
	ResponseEntity<ReadPaymentResponse> cancelPaymentRestClient(@RequestBody PaymentCancelRequest request) {
		return restClient.post()
			.uri(ApiUtils.getTossPaymentBasePath()+"/"+request.getPaymentKey()+"/cancel")
			.header(APPLICATION_JSON_VALUE)
			.header(HttpHeaders.AUTHORIZATION, "Basic " + authToken)
			.body(new TossPaymentCancelRequest(request.getCancelReason(), request.getCancelAmount()))
			.retrieve()
			.toEntity(ReadPaymentResponse.class);
	}
}
