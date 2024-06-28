package store.buzzbook.front.common.exception.cart;

public class NullCartException extends RuntimeException {
	public NullCartException() {
		super("장바구니가 없습니다!");
	}
}
