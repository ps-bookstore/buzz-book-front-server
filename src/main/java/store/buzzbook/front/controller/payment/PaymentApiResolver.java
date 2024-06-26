package store.buzzbook.front.controller.payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentApiResolver {
	private List<PaymentApiClient> paymentApiClients = new ArrayList<>();

	public PaymentApiResolver(List<PaymentApiClient> paymentApiClients) {
		this.paymentApiClients = paymentApiClients;
	}

	public PaymentApiClient getPaymentApiClient(String payType) {
		for (PaymentApiClient paymentApiClient : paymentApiClients) {
			if(paymentApiClient.matchPayType(payType)) {
				return paymentApiClient;
			}
		}

		throw new IllegalArgumentException("Not Supported payType");
	}
}
