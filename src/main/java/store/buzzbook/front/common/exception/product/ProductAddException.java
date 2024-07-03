package store.buzzbook.front.common.exception.product;

public class ProductAddException extends RuntimeException {

	public ProductAddException(String message) {
		super(message);
	}

	public ProductAddException(String message, Throwable cause) {
		super(message, cause);
	}
}