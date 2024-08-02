package store.buzzbook.front.dto.payment;

public class CardPayInfo extends PayInfo {
	private String cardName;

	public CardPayInfo(String orderId, int price, String paymentKey){
		super(orderId, price, PayType.신용카드, paymentKey);
	}
}
