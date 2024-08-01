package store.buzzbook.front.controller.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class PointPayInfo extends PayInfo {
	public PointPayInfo(String orderId, int price, String paymentKey) {
		super(orderId, price, PayType.POINT, paymentKey);
	}
}
