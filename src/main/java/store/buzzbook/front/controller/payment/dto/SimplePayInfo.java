package store.buzzbook.front.controller.payment.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SimplePayInfo extends PayInfo implements Serializable {
	@JsonCreator
	public SimplePayInfo(@JsonProperty("orderId") String orderId, @JsonProperty("totalAmount")int price, @JsonProperty("paymentKey") String paymentKey) {
		super(orderId, price, PayInfo.PayType.간편결제, paymentKey);
	}
}
