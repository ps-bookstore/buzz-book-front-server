package store.buzzbook.front.common.util;

public class ApiUtils {
	private ApiUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String getOrderBasePath() {
		return "http://localhost:8090/api/orders";
	}
	public static String getPaymentBasePath() {
		return "http://localhost:8090/api/payments";
	}
	public static String getTossPaymentBasePath() {
		return "https://api.tosspayments.com/v1/payments";
	}
}
