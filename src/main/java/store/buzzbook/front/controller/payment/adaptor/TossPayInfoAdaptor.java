package store.buzzbook.front.controller.payment.adaptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.front.controller.payment.dto.PayInfo;
import store.buzzbook.front.controller.payment.dto.PayResult;
import store.buzzbook.front.controller.payment.dto.SimplePayInfo;
import store.buzzbook.front.controller.payment.dto.TossPaymentResponse;

public class TossPayInfoAdaptor implements PayInfoAdaptor {

	private ObjectMapper objectMapper = new ObjectMapper();

	// private final TossPaymentResponse tossPaymentResponse;
	//
	// public TossPayInfoAdaptor(TossPaymentResponse tossPaymentResponse) {
	// 	this.tossPaymentResponse = tossPaymentResponse;
	// }

	@Override
	public SimplePayInfo convert(PayResult payResult) {
		SimplePayInfo simplePayInfo = objectMapper.convertValue(payResult, SimplePayInfo.class);
		return new SimplePayInfo(payResult.getOrderId(), payResult.getTotalAmount(), payResult.getPaymentKey());
	}

	@Override
	public PayInfo.PayType getPayType() {
		return PayInfo.PayType.간편결제;
	}
}
