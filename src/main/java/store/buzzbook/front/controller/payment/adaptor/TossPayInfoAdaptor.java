package store.buzzbook.front.controller.payment.adaptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.front.dto.payment.PayInfo;
import store.buzzbook.front.dto.payment.PayResult;
import store.buzzbook.front.dto.payment.SimplePayInfo;

public class TossPayInfoAdaptor implements PayInfoAdaptor {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public SimplePayInfo convert(PayResult payResult) {
		return objectMapper.convertValue(payResult, SimplePayInfo.class);

	}

	@Override
	public PayInfo.PayType getPayType() {
		return PayInfo.PayType.간편결제;
	}
}
