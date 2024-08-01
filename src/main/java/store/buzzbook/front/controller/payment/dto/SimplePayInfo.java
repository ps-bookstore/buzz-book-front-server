package store.buzzbook.front.controller.payment.dto;

import lombok.Getter;

@Getter
public class SimplePayInfo extends PayInfo {
	public SimplePayInfo(String orderId, int price) {
		super(orderId, price, PayInfo.PayType.간편결제);
	}
}
