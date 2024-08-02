package store.buzzbook.front.controller.payment.adaptor;

import store.buzzbook.front.dto.payment.PayInfo;
import store.buzzbook.front.dto.payment.PayResult;
import store.buzzbook.front.dto.payment.PointPayInfo;

public class PointPayInfoAdaptor implements PayInfoAdaptor {

	@Override
	public PointPayInfo convert(PayResult payResult) {
		return new PointPayInfo(payResult.getOrderId(), payResult.getTotalAmount(), payResult.getPaymentKey());
	}

	@Override
	public PayInfo.PayType getPayType() {
		return PayInfo.PayType.POINT;
	}
}
