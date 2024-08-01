package store.buzzbook.front.controller.payment.adaptor;

import store.buzzbook.front.controller.payment.dto.PayInfo;
import store.buzzbook.front.controller.payment.dto.PayResult;
import store.buzzbook.front.controller.payment.dto.PointPayInfo;
import store.buzzbook.front.controller.payment.dto.PointPaymentResponse;

public class PointPayInfoAdaptor implements PayInfoAdaptor {
	// private final PointPaymentResponse pointPaymentResponse;
	//
	// public PointPayInfoAdaptor(PointPaymentResponse pointPaymentResponse) {
	// 	this.pointPaymentResponse = pointPaymentResponse;
	// }

	@Override
	public PointPayInfo convert(PayResult payResult) {
		return new PointPayInfo(payResult.getOrderId(), payResult.getTotalAmount());
	}

	@Override
	public PayInfo.PayType getPayType() {
		return PayInfo.PayType.POINT;
	}
}
