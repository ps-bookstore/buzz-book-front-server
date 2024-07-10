package store.buzzbook.front.common.exception.user;

import lombok.Getter;

@Getter
public class DormantUserException extends RuntimeException {
	private final String dormantToken;

	public DormantUserException(String dormantToken) {
		super("휴면 계정의 로그인 요청입니다.");
		this.dormantToken = dormantToken;
	}
}
