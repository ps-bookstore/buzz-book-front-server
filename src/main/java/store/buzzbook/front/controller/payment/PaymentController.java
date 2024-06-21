package store.buzzbook.front.controller.payment;

import static org.springframework.http.MediaType.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClient;

import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.payment.PaymentConfirmationRequest;
import store.buzzbook.front.dto.payment.PaymentResponse;

@Controller
public class PaymentController {
	private static final String TOSS_PAYMENTS_API_URL = "https://api.tosspayments.com/v1/payments/confirm";

	@Autowired
	private RestClient restClient;

	@Value("${payment.auth-token}")
	private String authToken;

	@PostMapping("/confirm")
	public ResponseEntity<PaymentResponse> confirmPaymentRestClient(@ModelAttribute("createOrderRequest") CreateOrderRequest request) {

		return restClient.post()
			.uri(TOSS_PAYMENTS_API_URL)
			.header(APPLICATION_JSON_VALUE)
			.header(HttpHeaders.AUTHORIZATION, "Basic " + authToken)
			.body(PaymentConfirmationRequest.builder().paymentKey("fsdfskdfksdf").amount(100000).orderId("13231").build())
			.retrieve()
			.toEntity(PaymentResponse.class);
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

	// @RequestMapping(value = "/", method = RequestMethod.GET)
	// public String index(HttpServletRequest request, Model model) throws Exception {
	// 	return "/checkout";
	// }

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
}
