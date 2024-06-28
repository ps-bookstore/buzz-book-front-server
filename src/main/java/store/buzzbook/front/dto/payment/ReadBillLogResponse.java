package store.buzzbook.front.dto.payment;

import java.time.LocalDate;
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
public class ReadBillLogResponse {
	private long id;

	private String payment;
	private int price;
	private LocalDate paymentDate;

	private BillStatus status;

	private String paymentKey;
	private ReadOrderResponse readOrderResponse;
	private String cancelReason;
}
