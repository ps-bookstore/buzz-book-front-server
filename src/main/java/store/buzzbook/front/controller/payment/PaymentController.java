package store.buzzbook.front.controller.payment;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.annotation.JwtValidate;
import store.buzzbook.front.common.exception.order.CoreServerException;
import store.buzzbook.front.common.exception.user.UserTokenException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.payment.SimplePayInfo;
import store.buzzbook.front.dto.order.ReadOrderDetailResponse;
import store.buzzbook.front.dto.order.ReadOrderRequest;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.order.ReadOrderWithoutLoginRequest;
import store.buzzbook.front.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.front.dto.order.UpdateOrderRequest;
import store.buzzbook.front.dto.payment.BillStatus;
import store.buzzbook.front.dto.payment.CreateCancelBillLogRequest;
import store.buzzbook.front.dto.payment.ReadBillLogRequest;
import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.front.dto.payment.ReadPaymentKeyRequest;
import store.buzzbook.front.dto.payment.ReadPaymentKeyWithOrderDetailRequest;
import store.buzzbook.front.dto.payment.TossPaymentCancelRequest;
import store.buzzbook.front.service.payment.PaymentService;

/**
 * 결제 관련 컨트롤러
 *
 * @author 박설
 */

@Slf4j
@Controller
public class PaymentController {
	private static final String POINT = "POINT";
	private static final String CANCELED = "CANCELED";
	private static final String PARTIAL_CANCELED = "PARTIAL_CANCELED";
	private static final String POINT_ORDER_INQUIRY = "주문 시 포인트 적립";
	private static final String POINT_ORDER_POLICY = "전체상품";
	private static final String TOSS_PREFIX = "tviva";
	private static final String TOSS = "toss";
	private final PaymentService paymentService;

	private CookieUtils cookieUtils;
	@Value("${api.gateway.host}")
	private String host;

	@Value("${api.gateway.port}")
	private int port;
	
	PaymentApiResolver paymentApiResolver;

	public PaymentController(TossClient tossClient, CookieUtils cookieUtils, PaymentService paymentService) {
		paymentApiResolver = new PaymentApiResolver(List.of(tossClient));
		this.cookieUtils = cookieUtils;
		this.paymentService = paymentService;
	}

	@PostMapping("/confirm/{payType}")
	public ResponseEntity<JSONObject> confirmToss(@PathVariable String payType, @RequestBody String request) throws
		Exception {
		return paymentApiResolver.getPaymentApiClient(payType).confirm(request);
	}

	@PostMapping("/confirm")
	public ResponseEntity<JSONObject> confirm(@RequestBody String request) throws Exception {
		return ResponseEntity.ok(paymentService.confirm(request));
	}

	public ResponseEntity<JSONObject> cancel(@PathVariable String payType, @PathVariable String paymentKey,
		@RequestBody TossPaymentCancelRequest tossPaymentCancelRequest) throws InterruptedException {
		return paymentApiResolver.getPaymentApiClient(payType).cancel(paymentKey, tossPaymentCancelRequest);
	}

	/**
	 * 인증성공처리
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/success")
	public String successPayment(HttpServletRequest request, Model model, @RequestParam("orderId") String orderId,
		@RequestParam String paymentKey, @RequestParam Integer amount, @RequestParam("customerEmail") String customerEmail) {

		Optional<Cookie> cookie = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);

		ReadOrderRequest readOrderRequest = new ReadOrderRequest();
		readOrderRequest.setOrderId(orderId);

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		if (cookie.isEmpty()) {
			ReadOrderWithoutLoginRequest readOrderWithoutLoginRequest = ReadOrderWithoutLoginRequest.builder()
				.orderId(orderId)
				.orderEmail(customerEmail)
				.build();
			HttpEntity<ReadOrderWithoutLoginRequest> readOrderRequestHttpEntity = new HttpEntity<>(readOrderWithoutLoginRequest, headers);

			ResponseEntity<ReadOrderResponse> responseResponseEntity = restTemplate.exchange(
				String.format("http://%s:%d/api/orders/non-member", host, port), HttpMethod.POST, readOrderRequestHttpEntity,
				ReadOrderResponse.class);


			model.addAttribute("title", "결제 성공");
			model.addAttribute("orderResult", responseResponseEntity.getBody());
			model.addAttribute("page", "success");

			return "index";
		}

		HttpEntity<ReadOrderRequest> readOrderRequestHttpEntity = new HttpEntity<>(readOrderRequest, headers);

		Optional<Cookie> jwt = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		if(jwt.isEmpty()|| refresh.isEmpty()) {
			throw new UserTokenException();
		}

		String accessToken = String.format("Bearer %s", jwt.get().getValue());
		String refreshToken = String.format("Bearer %s", refresh.get().getValue());

		headers.set(CookieUtils.COOKIE_JWT_ACCESS_KEY, accessToken);
		headers.set(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);

		ResponseEntity<ReadOrderResponse> responseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/id", host, port), HttpMethod.POST, readOrderRequestHttpEntity,
			ReadOrderResponse.class);

		// 성공 페이지를 로드하는 컨트롤러에서 포인트 결제 내역을 생성하는 코드
		// 포인트 amount, 결제 수단, paymentKey, 주문번호(코드)
		// payments/bill-log/different-payment
		// -> /orders/{order-id}/succes
		/*{
		 	결재vender : 토스,
		 	결재수단 : 신용카드,
		 	카드사 : 신한카드
		 	결재번호 : 1111,
		 	결재금액 : 12000,

		}
		// 계좌이체
		 {
		 	은행 : 농협은행
		 	결재금액 :1000,
		 */

		// -> toss 결재 성공 -> 결과값
		// 쿠폰, 포인트 <-- 보낼 필요 없음 -> 주문서 상에 존재

		model.addAttribute("title", "결제 성공");
		model.addAttribute("orderResult", responseResponseEntity.getBody());
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
	public String failPayment(HttpServletRequest request, Model model) {
		String failCode = request.getParameter("code");
		String failMessage = request.getParameter("message");

		model.addAttribute("code", failCode);
		model.addAttribute("message", failMessage);
		model.addAttribute("page", "fail");
		model.addAttribute("title", "결제 실패");

		return "index";
	}

	@JwtValidate
	@GetMapping("/mybilllogs")
	public String myPayment(Model model, @RequestParam String orderId, HttpServletRequest request) {

		ReadBillLogRequest readBillLogRequest = new ReadBillLogRequest(orderId);

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		Optional<Cookie> jwt = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		if(jwt.isEmpty()|| refresh.isEmpty()) {
			throw new UserTokenException();
		}

		String accessToken = String.format("Bearer %s", jwt.get().getValue());
		String refreshToken = String.format("Bearer %s", refresh.get().getValue());

		headers.set(CookieUtils.COOKIE_JWT_ACCESS_KEY, accessToken);
		headers.set(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);

		HttpEntity<ReadBillLogRequest> readBillLogRequestHttpEntity = new HttpEntity<>(readBillLogRequest, headers);

		ResponseEntity<List<ReadBillLogWithoutOrderResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-logs", host, port), HttpMethod.POST,
			readBillLogRequestHttpEntity, new ParameterizedTypeReference<List<ReadBillLogWithoutOrderResponse>>() {
			});

		model.addAttribute("myBillLogs", response.getBody());
		model.addAttribute("page", "mybilllog");
		model.addAttribute("title", "내 결제 정보");

		return "index";
	}

	@GetMapping("/nonMemberBilllogs")
	public String nonMemberPayment(Model model, @RequestParam String orderId) {

		ReadBillLogRequest readBillLogRequest = new ReadBillLogRequest(orderId);

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<ReadBillLogRequest> readBillLogRequestHttpEntity = new HttpEntity<>(readBillLogRequest, headers);

		ResponseEntity<List<ReadBillLogWithoutOrderResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-logs", host, port), HttpMethod.POST,
			readBillLogRequestHttpEntity, new ParameterizedTypeReference<List<ReadBillLogWithoutOrderResponse>>() {
			});

		model.addAttribute("myBillLogs", response.getBody());
		model.addAttribute("page", "mybilllog");
		model.addAttribute("title", "비회원 결제 정보");

		return "index";
	}

	@GetMapping("/myorder/cancel")
	public String cancelOrderBeforeShipping(Model model,
		@RequestParam("id") String orderId,
		@RequestParam String cancelReason,
		@RequestParam int page,
		@RequestParam int size, HttpServletRequest request) throws InterruptedException {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		Optional<Cookie> jwt = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		if(jwt.isEmpty()|| refresh.isEmpty()) {
			throw new UserTokenException();
		}

		String accessToken = String.format("Bearer %s", jwt.get().getValue());
		String refreshToken = String.format("Bearer %s", refresh.get().getValue());

		headers.set(CookieUtils.COOKIE_JWT_ACCESS_KEY, accessToken);
		headers.set(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);

		TossPaymentCancelRequest tossPaymentCancelRequest = TossPaymentCancelRequest.builder()
					.cancelReason(cancelReason)
						.build();

		HttpEntity<ReadPaymentKeyRequest> readPaymentKeyRequestHttpEntity = new HttpEntity<>(
			ReadPaymentKeyRequest.builder().orderId(orderId).build(), headers);

		ResponseEntity<String> paymentKey = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/payment-key", host, port), HttpMethod.POST, readPaymentKeyRequestHttpEntity,
			String.class);

		if (paymentKey.getBody().startsWith(TOSS_PREFIX)) {
			String jsonObject = Objects.requireNonNull(
				cancel(TOSS, paymentKey.getBody(), tossPaymentCancelRequest).getBody()).toString();
		}

		return "redirect:/orders?page=" + page + "&size=10";
	}

	@GetMapping("/myorderdetail/cancel/{payType}")
	public String cancelOrderDetailBeforeShipping(Model model, @RequestParam("id") long orderDetailId,
		@PathVariable String payType,
		@RequestParam String cancelReason,
		@RequestParam int page,
		@RequestParam int size, HttpServletRequest request) throws InterruptedException {

		UpdateOrderDetailRequest updateOrderDetailRequest = UpdateOrderDetailRequest.builder()
			.id(orderDetailId)
			.orderStatusName(PARTIAL_CANCELED)
			.build();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		Optional<Cookie> jwt = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		if(jwt.isEmpty()|| refresh.isEmpty()) {
			throw new UserTokenException();
		}

		String accessToken = String.format("Bearer %s", jwt.get().getValue());
		String refreshToken = String.format("Bearer %s", refresh.get().getValue());

		headers.set(CookieUtils.COOKIE_JWT_ACCESS_KEY, accessToken);
		headers.set(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);

		HttpEntity<UpdateOrderDetailRequest> updateOrderRequestHttpEntity = new HttpEntity<>(updateOrderDetailRequest, headers);
		ResponseEntity<ReadOrderDetailResponse> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/detail", host, port), HttpMethod.PUT, updateOrderRequestHttpEntity,
			ReadOrderDetailResponse.class);

		TossPaymentCancelRequest tossPaymentCancelRequest = TossPaymentCancelRequest.builder()
			.cancelAmount(response.getBody().getPrice())
			.cancelReason(cancelReason)
			.build();

		HttpEntity<ReadPaymentKeyWithOrderDetailRequest> readPaymentKeyWithOrderDetailRequestHttpEntity = new HttpEntity<>(
			ReadPaymentKeyWithOrderDetailRequest.builder().orderDetailId(orderDetailId).build(), headers);
		ResponseEntity<String> paymentKey = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/detail/payment-key", host, port), HttpMethod.POST, readPaymentKeyWithOrderDetailRequestHttpEntity,
			String.class);

		String jsonObject = Objects.requireNonNull(
			cancel(payType, paymentKey.getBody(), tossPaymentCancelRequest).getBody()).toString();

		HttpEntity<String> jsonObjectHttpEntity = new HttpEntity<>(jsonObject, headers);

		ResponseEntity<ReadBillLogResponse> paymentResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-log/cancel", host, port), HttpMethod.POST, jsonObjectHttpEntity,
			ReadBillLogResponse.class);

		CreateCancelBillLogRequest createCancelBillLogRequest = CreateCancelBillLogRequest.builder()
			.cancelReason(paymentResponse.getBody().getCancelReason()).status(
				BillStatus.PARTIAL_CANCELED).build();

		HttpEntity<CreateCancelBillLogRequest> createCancelBillLogRequestHttpEntity = new HttpEntity<>(createCancelBillLogRequest, headers);

		performExchange(() ->
			restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-log/different-payment/cancel", host, port), HttpMethod.POST, createCancelBillLogRequestHttpEntity,
			String.class)
		);

		return "redirect:/orders?page=" + page + "&size=10";
	}

	@GetMapping("/myorder/refund")
	public String refundOrder(@RequestParam("id") String orderId, @RequestParam("price") String price, @RequestParam("orderStatus") String orderStatus,
		@RequestParam int page,
		@RequestParam int size, HttpServletRequest request) {

		UpdateOrderRequest updateOrderRequest = UpdateOrderRequest.builder()
			.orderId(orderId)
			.orderStatusName(orderStatus)
			.build();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		Optional<Cookie> jwt = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		if(jwt.isEmpty()|| refresh.isEmpty()) {
			throw new UserTokenException();
		}

		String accessToken = String.format("Bearer %s", jwt.get().getValue());
		String refreshToken = String.format("Bearer %s", refresh.get().getValue());

		headers.set(CookieUtils.COOKIE_JWT_ACCESS_KEY, accessToken);
		headers.set(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);

		HttpEntity<ReadPaymentKeyRequest> readPaymentKeyRequestHttpEntity = new HttpEntity<>(
			ReadPaymentKeyRequest.builder().orderId(orderId).build(), headers);
		ResponseEntity<String> paymentKey = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/payment-key", host, port), HttpMethod.POST, readPaymentKeyRequestHttpEntity,
			String.class);

		SimplePayInfo payInfo = new SimplePayInfo(orderId, Integer.parseInt(price), paymentKey.getBody());

		CreateCancelBillLogRequest createCancelBillLogRequest = CreateCancelBillLogRequest.builder()
			.payInfo(payInfo).status(BillStatus.REFUND).orderId(orderId).build();

		HttpEntity<CreateCancelBillLogRequest> createCancelBillLogRequestHttpEntity = new HttpEntity<>(createCancelBillLogRequest, headers);

		ResponseEntity<String> billLogResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/refund", host, port), HttpMethod.POST, createCancelBillLogRequestHttpEntity,
			String.class);

		if (!billLogResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
			String errorMessage = String.format("Failed to cancel bill log. Status code: %d, Response body: %s",
				billLogResponseResponseEntity.getStatusCode().value(),
				billLogResponseResponseEntity.getBody());
			log.error(errorMessage);
			throw new CoreServerException("환불 실패");
		}

		return "redirect:/orders?page=" + page + "&size=10";
	}

	@Retryable(
		retryFor = { Exception.class },
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000)
	)
	public <T> void performExchange(Supplier<ResponseEntity<T>> requestSupplier) {
		requestSupplier.get();
	}

	@GetMapping("/nonMemberOrder/cancel")
	public String cancelNonMemberOrderBeforeShipping(Model model,
		@RequestParam("id") String orderId,
		@RequestParam String orderEmail,
		@RequestParam String cancelReason,
		HttpServletRequest request) throws InterruptedException {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		TossPaymentCancelRequest tossPaymentCancelRequest = TossPaymentCancelRequest.builder()
			.cancelReason(cancelReason)
			.build();

		HttpEntity<ReadPaymentKeyRequest> readPaymentKeyRequestHttpEntity = new HttpEntity<>(
			ReadPaymentKeyRequest.builder().orderId(orderId).orderEmail(orderEmail).build(), headers);
		ResponseEntity<String> paymentKey = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/payment-key", host, port), HttpMethod.POST, readPaymentKeyRequestHttpEntity,
			String.class);

		if (paymentKey.getBody().startsWith(TOSS_PREFIX)) {
			String jsonObject = Objects.requireNonNull(
				cancel(TOSS, paymentKey.getBody(), tossPaymentCancelRequest).getBody()).toString();
		}

		return "redirect:/nonMemberOrder";
	}
}
