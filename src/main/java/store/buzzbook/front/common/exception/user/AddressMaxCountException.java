package store.buzzbook.front.common.exception.user;

public class AddressMaxCountException extends RuntimeException {
	public AddressMaxCountException() {
		super("해당 회원의 주소는 이미 10개입니다.");
	}
}
