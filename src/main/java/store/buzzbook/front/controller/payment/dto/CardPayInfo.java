package store.buzzbook.front.controller.payment.dto;

public class CardPayInfo extends PayInfo {
	private String cardName;

	public CardPayInfo(String orderId, int price){
		super(orderId, price, PayType.신용카드);
	}
}
