package store.buzzbook.front.common.util;

public class ApiUtils {
	private ApiUtils() {
		throw new IllegalStateException("Utility class");
	}
	public static String getAccountBasePath() {
		return "http://localhost:3000/api/account";
	}
	public static String getOrderBasePath() {
		return "http://localhost:3001/api/orders";
	}
	public static String getPaymentBasePath() {
		return "http://localhost:3001/api/payments";
	}
}
