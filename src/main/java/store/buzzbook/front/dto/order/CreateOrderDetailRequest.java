package store.buzzbook.front.dto.order;

import java.time.ZonedDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOrderDetailRequest {
	private int price;
	private int quantity;
	private boolean wrap;
	private ZonedDateTime createDate;
	private int orderStatusId;
	private Integer wrappingId;
	private int productId;
	private long orderId;
}
