package store.buzzbook.front.controller.payment;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;

import store.buzzbook.front.dto.order.OrderFormData;
import store.buzzbook.front.dto.payment.PaymentConfirmationRequest;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;


@Controller
public class PaymentController {
	private TossClient tossClient;
	PaymentApiResolver paymentApiResolver;

	public PaymentController(TossClient tossClient) {
		this.tossClient = tossClient;
		paymentApiResolver = new PaymentApiResolver(List.of(tossClient));
	}

	@PostMapping("/confirm/{payType}")
	public ResponseEntity<ReadPaymentResponse> transferRequest(@PathVariable String payType, @ModelAttribute("orderFormData") OrderFormData orderFormData) {
		PaymentConfirmationRequest request = new PaymentConfirmationRequest();
		request.setAmount(Integer.valueOf(orderFormData.getPrice().replace(",", "")));
		request.setPaymentKey(UUID.randomUUID().toString());
		request.setOrderId(orderFormData.getOrderStr());
		return paymentApiResolver.getPaymentApiClient(payType).confirm(request);
	}

	@GetMapping("/payments/{payType}/{paymentKey}")
	public ResponseEntity<ReadPaymentResponse> getPayment(@PathVariable String payType, @PathVariable String paymentKey) {
		return paymentApiResolver.getPaymentApiClient(payType).read(paymentKey);
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
}
