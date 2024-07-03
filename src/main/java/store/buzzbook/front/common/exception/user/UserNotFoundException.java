package store.buzzbook.front.common.exception.user;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(Long userId) {
		super(String.format("회원 아이디로 조회 실패: %s",userId));
	}

	public UserNotFoundException(String loginId) {
		super(String.format("회원의 로그인 아이디로 조회 실패: %s",loginId));
	}
}
