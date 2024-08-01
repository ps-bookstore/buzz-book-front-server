package store.buzzbook.front.controller.payment.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class PayInfo {
	public enum PayType {
		간편결제("간편결제"), POINT("POINT"), 신용카드("CARD");

		private String value;

		PayType(String value) {
			this.value = value;
		}

		public static PayType fromValue(String value) {
			for (PayType payType : PayType.values()) {
				if (payType.value.equals(value)) {
					return payType;
				}
			}
			throw new IllegalArgumentException("Unknown value: " + value);
		}
	}

	private final String orderId;
	private final int price;
	private final PayType payType;

	public PayInfo(String orderId, int price, PayType payType) {
		this.orderId = orderId;
		this.price = price;
		this.payType = payType;
	}
}
