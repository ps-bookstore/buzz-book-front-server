package store.buzzbook.front.common.exception.cart;

public class CartNotFoundException extends RuntimeException {
	public CartNotFoundException() {
		super("잘못된 장바구니 요청입니다.");
	}
}
