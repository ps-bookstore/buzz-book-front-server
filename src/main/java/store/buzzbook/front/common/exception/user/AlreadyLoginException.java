package store.buzzbook.front.common.exception.user;

public class AlreadyLoginException extends RuntimeException {
	public AlreadyLoginException() {
		super("이미 로그인되어 접근할 수 없는 서비스입니다.");
	}
}
