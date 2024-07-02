package store.buzzbook.front.controller.payment;

import java.net.http.HttpResponse;
import java.util.List;

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

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpSession;
import store.buzzbook.front.dto.order.ReadOrderRequest;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.payment.ReadBillLogRequest;
import store.buzzbook.front.dto.payment.ReadBillLogWithoutOrderResponse;

@Controller
public class PaymentController {
	@Value("${api.core.host}")
	private String host;

	@Value("${api.core.port}")
	private int port;

	private TossClient tossClient;
	PaymentApiResolver paymentApiResolver;

	public PaymentController(TossClient tossClient) {
		this.tossClient = tossClient;
		paymentApiResolver = new PaymentApiResolver(List.of(tossClient));
	}

	@PostMapping("/confirm/{payType}")
	public ResponseEntity<JSONObject> confirm(@PathVariable String payType, @RequestBody String request) throws Exception {
		return paymentApiResolver.getPaymentApiClient(payType).confirm(request);
	}

	@GetMapping("/payments/{payType}/{paymentKey}")
	public ResponseEntity<JSONObject> getPayment(@PathVariable String payType, @PathVariable String paymentKey) {
		return paymentApiResolver.getPaymentApiClient(payType).read(paymentKey);
	}

	@PostMapping("/payments/{payType}/{paymentKey}/cancel")
	public HttpResponse<String> cancel(@PathVariable String payType, @PathVariable String paymentKey, @RequestParam String cancelReason) throws Exception {
		return paymentApiResolver.getPaymentApiClient(payType).cancel(paymentKey, cancelReason);
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
		@RequestParam String paymentType, @RequestParam String paymentKey, @RequestParam Integer amount) throws Exception {
		ReadOrderRequest readOrderRequest = new ReadOrderRequest();
		readOrderRequest.setLoginId("parkseol");
		readOrderRequest.setOrderId(orderId);

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<ReadOrderRequest> readOrderRequeset = new HttpEntity<>(readOrderRequest, headers);

		ResponseEntity<ReadOrderResponse> responseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/id", host, port), HttpMethod.POST, readOrderRequeset, ReadOrderResponse.class);

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
	public String failPayment(HttpServletRequest request, Model model) throws Exception {
		String failCode = request.getParameter("code");
		String failMessage = request.getParameter("message");

		model.addAttribute("code", failCode);
		model.addAttribute("message", failMessage);
		model.addAttribute("page", "fail");

		return "index";
	}

	@GetMapping("/mybilllogs")
	public String myPayment(HttpSession session, Model model, @RequestParam String orderId) throws Exception {

		ReadBillLogRequest request = new ReadBillLogRequest(orderId, "parkseol");

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<ReadBillLogRequest> readBillLogRequestHttpEntity = new HttpEntity<>(request, headers);

		ResponseEntity<List<ReadBillLogWithoutOrderResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-logs", host, port), HttpMethod.POST, readBillLogRequestHttpEntity, new ParameterizedTypeReference<List<ReadBillLogWithoutOrderResponse>>() {});

		model.addAttribute("myBillLogs", response.getBody());
		model.addAttribute("page", "mybilllog");

		return "index";
	}

}
