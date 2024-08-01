package store.buzzbook.front.controller.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public abstract class PayResult {
	@JsonProperty("method")
	private String method;
	@JsonProperty("totalAmount")
	private int totalAmount;
	@JsonProperty("paymentKey")
	private String paymentKey;
	@JsonProperty("orderId")
	private String orderId;
}
