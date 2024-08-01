package store.buzzbook.front.controller.payment.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PayInfo implements Serializable {

	public enum PayType {
		간편결제("간편결제"), POINT("POINT"), 신용카드("CARD");

		private String value;

		PayType(String value) {
			this.value = value;
		}

		@JsonCreator
		public static PayType fromValue(String value) {
			for (PayType payType : PayType.values()) {
				if (payType.value.equals(value)) {
					return payType;
				}
			}
			throw new IllegalArgumentException("Unknown value: " + value);
		}
	}
	@JsonProperty("orderId")
	private final String orderId;
	@JsonProperty("totalAmount")
	private final int price;
	@JsonProperty("method")
	private final PayType payType;
	@JsonProperty("paymentKey")
	private final String paymentKey;

	public PayInfo(String orderId, int price, PayType payType, String paymentKey) {
		this.orderId = orderId;
		this.price = price;
		this.payType = payType;
		this.paymentKey = paymentKey;
	}
}
