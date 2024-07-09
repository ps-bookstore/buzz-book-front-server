package store.buzzbook.front.controller.payment;

import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
import store.buzzbook.front.common.exception.user.UserTokenException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.order.ReadOrderDetailResponse;
import store.buzzbook.front.dto.order.ReadOrderRequest;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.order.ReadOrderWithoutLoginRequest;
import store.buzzbook.front.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.front.dto.order.UpdateOrderRequest;
import store.buzzbook.front.dto.payment.BillStatus;
import store.buzzbook.front.dto.payment.CreateBillLogRequest;
import store.buzzbook.front.dto.payment.CreateCancelBillLogRequest;
import store.buzzbook.front.dto.payment.ReadBillLogRequest;
import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.front.dto.payment.ReadPaymentKeyRequest;
import store.buzzbook.front.dto.payment.ReadPaymentKeyWithOrderDetailRequest;
import store.buzzbook.front.dto.payment.TossPaymentCancelRequest;

@Slf4j
@Controller
public class PaymentController {
	private static final String POINT = "POINT";

	private CookieUtils cookieUtils;
	@Value("${api.gateway.host}")
	private String host;

	@Value("${api.gateway.port}")
	private int port;
	
	PaymentApiResolver paymentApiResolver;

	public PaymentController(TossClient tossClient, CookieUtils cookieUtils) {
		paymentApiResolver = new PaymentApiResolver(List.of(tossClient));
		this.cookieUtils = cookieUtils;
	}

	@PostMapping("/confirm/{payType}")
	public ResponseEntity<JSONObject> confirm(@PathVariable String payType, @RequestBody String request) throws
		Exception {
		return paymentApiResolver.getPaymentApiClient(payType).confirm(request);
	}

	@GetMapping("/payments/{payType}/{paymentKey}")
	public ResponseEntity<JSONObject> getPayment(@PathVariable String payType, @PathVariable String paymentKey) {
		return paymentApiResolver.getPaymentApiClient(payType).read(paymentKey);
	}

	@PostMapping("/payments/{payType}/{paymentKey}/cancel")
	public ResponseEntity<JSONObject> cancel(@PathVariable String payType, @PathVariable String paymentKey,
		@RequestBody TossPaymentCancelRequest tossPaymentCancelRequest) {
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
		@RequestParam String paymentType, @RequestParam String paymentKey, @RequestParam Integer amount,
		@RequestParam("customerEmail") String customerEmail, @RequestParam("myPoint") String myPoint, @RequestParam("couponCode") String couponCode) throws
		Exception {

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

		if (Integer.parseInt(myPoint) != 0) {
			CreateBillLogRequest createBillLogRequest = CreateBillLogRequest.builder()
				.price(Integer.parseInt(myPoint)).payment(POINT).paymentKey(paymentKey).orderId(orderId).build();

			HttpEntity<CreateBillLogRequest> createBillLogRequestHttpEntity = new HttpEntity<>(createBillLogRequest, headers);

			ResponseEntity<ReadBillLogResponse> billLogResponseResponseEntity = restTemplate.exchange(
				String.format("http://%s:%d/api/payments/bill-log/different-payment", host, port), HttpMethod.POST, createBillLogRequestHttpEntity,
				ReadBillLogResponse.class);
		}

		if (!couponCode.isEmpty()) {
			CreateBillLogRequest createBillLogRequest = CreateBillLogRequest.builder()
				.price(Integer.parseInt(myPoint)).payment(couponCode).paymentKey(paymentKey).orderId(orderId).build();

			HttpEntity<CreateBillLogRequest> createBillLogRequestHttpEntity = new HttpEntity<>(createBillLogRequest, headers);

			ResponseEntity<ReadBillLogResponse> billLogResponseResponseEntity = restTemplate.exchange(
				String.format("http://%s:%d/api/payments/bill-log/different-payment", host, port), HttpMethod.POST, createBillLogRequestHttpEntity,
				ReadBillLogResponse.class);

			HttpEntity<Object> deleteUserCouponRequestHttpEntity = new HttpEntity<>(headers);

			ResponseEntity<Void> deleteUserCouponResponseEntity = restTemplate.exchange(
				String.format("http://%s:%d/api/account/coupons/order?couponCode=%s", host, port, couponCode),
				HttpMethod.DELETE, deleteUserCouponRequestHttpEntity,
				Void.class);
		}

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

		return "index";
	}

	@JwtValidate
	@GetMapping("/mybilllogs")
	public String myPayment(Model model, @RequestParam String orderId, HttpServletRequest request) throws Exception {

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

		return "index";
	}

	@GetMapping("/nonMemberBilllogs")
	public String nonMemberPayment(Model model, @RequestParam String orderId) throws Exception {

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

		return "index";
	}

	@GetMapping("/myorder/cancel/{payType}")
	public String cancelOrderBeforeShipping(Model model,
		@PathVariable String payType,
		@RequestParam("id") String orderId,
		@RequestParam String cancelReason,
		@RequestParam int page,
		@RequestParam int size, HttpServletRequest request) throws Exception {

		UpdateOrderRequest updateOrderRequest = UpdateOrderRequest.builder()
			.orderId(orderId)
			.orderStatusName("CANCELED")
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

		HttpEntity<UpdateOrderRequest> updateOrderRequestHttpEntity = new HttpEntity<>(updateOrderRequest, headers);
		ResponseEntity<ReadOrderResponse> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders", host, port), HttpMethod.PUT, updateOrderRequestHttpEntity,
			ReadOrderResponse.class);

		TossPaymentCancelRequest tossPaymentCancelRequest = TossPaymentCancelRequest.builder()
				.cancelAmount(response.getBody().getPrice())
					.cancelReason(cancelReason)
						.build();

		HttpEntity<ReadPaymentKeyRequest> readPaymentKeyRequestHttpEntity = new HttpEntity<>(
			ReadPaymentKeyRequest.builder().orderId(orderId).build(), headers);
		ResponseEntity<String> paymentKey = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/payment-key", host, port), HttpMethod.POST, readPaymentKeyRequestHttpEntity,
			String.class);

		JSONObject jsonObject = cancel(payType, paymentKey.getBody(), tossPaymentCancelRequest).getBody();

		HttpEntity<JSONObject> jsonObjectHttpEntity = new HttpEntity<>(jsonObject, headers);

		ResponseEntity<ReadBillLogResponse> paymentResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-log/cancel", host, port), HttpMethod.POST, jsonObjectHttpEntity,
			ReadBillLogResponse.class);

		return "redirect:/my-page?page=" + page + "&size=10";
	}

	@GetMapping("/myorderdetail/cancel/{payType}")
	public String cancelOrderDetailBeforeShipping(Model model, @RequestParam("id") long orderDetailId,
		@PathVariable String payType,
		@RequestParam String cancelReason,
		@RequestParam int page,
		@RequestParam int size, HttpServletRequest request) throws Exception {

		UpdateOrderDetailRequest updateOrderDetailRequest = UpdateOrderDetailRequest.builder()
			.id(orderDetailId)
			.orderStatusName("CANCELED")
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

		JSONObject jsonObject = cancel(payType, paymentKey.getBody(), tossPaymentCancelRequest).getBody();

		HttpEntity<JSONObject> jsonObjectHttpEntity = new HttpEntity<>(jsonObject, headers);

		ResponseEntity<ReadBillLogResponse> paymentResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-log/cancel", host, port), HttpMethod.POST, jsonObjectHttpEntity,
			ReadBillLogResponse.class);

		CreateCancelBillLogRequest createCancelBillLogRequest = CreateCancelBillLogRequest.builder()
			.cancelReason(paymentResponse.getBody().getCancelReason()).paymentKey(paymentKey.getBody()).status(
				BillStatus.PARTIAL_CANCELED).build();

		HttpEntity<CreateCancelBillLogRequest> createCancelBillLogRequestHttpEntity = new HttpEntity<>(createCancelBillLogRequest, headers);

		ResponseEntity<String> billLogResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-log/different-payment/cancel", host, port), HttpMethod.POST, createCancelBillLogRequestHttpEntity,
			String.class);

		return "redirect:/my-page?page=" + page + "&size=10";
	}
}
