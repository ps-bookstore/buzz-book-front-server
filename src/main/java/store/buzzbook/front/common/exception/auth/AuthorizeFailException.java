package store.buzzbook.front.common.exception.auth;

public class AuthorizeFailException extends RuntimeException {
	public AuthorizeFailException(String message) {
		super(message);
	}

	public AuthorizeFailException(String error, String message) {
		super(String.format("%s: %s", error, message));
	}

}
