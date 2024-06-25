package store.buzzbook.front.common.exception.product;

public class ProductNotFoundException extends RuntimeException{

	public ProductNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
