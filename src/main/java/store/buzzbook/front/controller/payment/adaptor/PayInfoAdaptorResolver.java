package store.buzzbook.front.controller.payment.adaptor;

import java.util.ArrayList;
import java.util.List;

import store.buzzbook.front.dto.payment.PayInfo;

public class PayInfoAdaptorResolver {
	private List<PayInfoAdaptor> adaptors = new ArrayList<>();

	public PayInfoAdaptorResolver(List<PayInfoAdaptor> adaptorList) {
		adaptors.addAll(adaptorList);
	}

	public PayInfoAdaptor getPayInfoAdaptor(PayInfo.PayType payType) {
		for (PayInfoAdaptor adaptor : adaptors) {
			if (adaptor.matchPayType(payType)) {
				return adaptor;
			}
		}

		throw new IllegalArgumentException("PayInfoAdaptor '" + payType + "' not found");
	}
}
