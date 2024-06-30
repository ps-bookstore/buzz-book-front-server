package store.buzzbook.front.common.exception.user;

public class PasswordNotConfirmedException extends RuntimeException {
	public PasswordNotConfirmedException() {
		super("입력한 비밀번호와 비밀번호 확인이 다릅니다.");
	}

	public PasswordNotConfirmedException(String message) {
		super(message);
	}
}
