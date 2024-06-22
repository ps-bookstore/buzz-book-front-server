package store.buzzbook.front.dto.payment;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.front.dto.order.ReadOrderResponse;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BillLogResponse {
	private long id;

	private String payment;
	private int price;
	private ZonedDateTime paymentDate;

	private BillStatus status;

	private UUID paymentKey;
	private ReadOrderResponse readOrderResponse;
}
