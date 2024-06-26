package store.buzzbook.front.controller.payment;

import static org.springframework.http.MediaType.*;

import java.util.List;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.util.ApiUtils;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.OrderFormData;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.payment.PaymentCancelRequest;
import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.PaymentConfirmationRequest;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;
import store.buzzbook.front.dto.payment.TossPaymentCancelRequest;

@Controller
@RequiredArgsConstructor
public class PaymentController {

	private RestClient restClient;

	PaymentApiResolver paymentApiResolver;

	private final TossClient tossClient;

	@PostMapping("/confirm/{payType}")
	public ResponseEntity<ReadPaymentResponse> transferRequest(@PathVariable String payType, @ModelAttribute("orderFormData") OrderFormData orderFormData) {

		paymentApiResolver = new PaymentApiResolver(List.of(tossClient));
		return paymentApiResolver.getPaymentApiClient(payType).confirm(orderFormData);
	}


	@PostMapping("billLog/register")
	public ResponseEntity<ReadBillLogResponse> createBillLogRestClient(@RequestBody ReadPaymentResponse response) {

		return restClient.post()
			.uri(ApiUtils.getPaymentBasePath()+"/bill-log")
			.header(APPLICATION_JSON_VALUE)
			.body(response)
			.retrieve()
			.toEntity(ReadBillLogResponse.class);
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
