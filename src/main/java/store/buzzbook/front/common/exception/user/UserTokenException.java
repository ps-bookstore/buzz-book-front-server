package store.buzzbook.front.common.exception.user;

public class UserTokenException extends RuntimeException {
	public UserTokenException() {
		super("잘못된 토큰이거나 유저 정보 가져오는데 문제가 발생했습니다.");
	}
}
