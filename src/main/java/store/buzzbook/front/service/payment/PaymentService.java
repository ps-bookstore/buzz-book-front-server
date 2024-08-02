package store.buzzbook.front.service.payment;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.exception.order.JSONParsingException;
import store.buzzbook.front.dto.payment.PointPaymentResponse;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PayResultService payResultService;
	private final JSONParser parser = new JSONParser();

	public JSONObject confirm(String request) {
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
			throw new JSONParsingException("포인트 결제 요청중 json 파싱 오류");
		}

		PointPaymentResponse pointPaymentResponse = new PointPaymentResponse(Integer.parseInt(amount), paymentKey, orderId);

		payResultService.pointOrder(orderId, pointPaymentResponse);

		JSONObject response = new JSONObject();
		response.put("amount", Integer.parseInt(amount));
		response.put("paymentKey", paymentKey);
		response.put("orderId", orderId);
		response.put("status", "SUCCESS");

		return response;
	}
}
