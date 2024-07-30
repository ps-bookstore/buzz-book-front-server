package store.buzzbook.front.controller.payment;

import lombok.Getter;

@Getter
public class PayInfo {
	enum PayType {
		신용카드,계좌이체
	}

	private final long orderId;
	//전체 결제 금액
	private final int totalPrice;

	//결제 수단
	private final PayType payType;

	public PayInfo(long orderId, int totalPrice, PayType payType){
		this.orderId = orderId;
		this.totalPrice=totalPrice;
		this.payType=payType;
	}



}
