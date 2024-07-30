package store.buzzbook.front.controller.payment;

public class CardPayInfo extends PayInfo {
	//신용카드에 관련된 정보
	private String cardName;

	public CardPayInfo(long orderId, int totalPrice){
		super(orderId,totalPrice,PayType.신용카드);
	}

}
