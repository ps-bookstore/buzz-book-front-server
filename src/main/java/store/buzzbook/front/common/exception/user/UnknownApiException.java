package store.buzzbook.front.common.exception.user;

public class UnknownApiException extends RuntimeException {
	public UnknownApiException(String api) {
		super(String.format("%s api : 알 수 없는 오류가 발생했습니다.", api));
	}
}
