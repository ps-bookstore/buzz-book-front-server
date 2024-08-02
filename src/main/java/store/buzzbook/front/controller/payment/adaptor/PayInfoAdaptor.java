package store.buzzbook.front.controller.payment.adaptor;

import store.buzzbook.front.dto.payment.PayInfo;
import store.buzzbook.front.dto.payment.PayResult;

public interface PayInfoAdaptor {
	PayInfo convert(PayResult payResult);
	PayInfo.PayType getPayType();
	default boolean matchPayType(PayInfo.PayType payType) {
		return payType == getPayType();
	}
}
