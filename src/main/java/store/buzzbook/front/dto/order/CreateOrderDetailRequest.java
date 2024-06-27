package store.buzzbook.front.dto.order;

import java.time.ZonedDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.buzzbook.front.dto.product.ProductResponse;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOrderDetailRequest {
	private int price;
	private int quantity;
	private boolean wrap = true;
	private int orderStatusId;
	private Integer wrappingId;
	private ProductResponse productResponse;
	private Long orderId;
}
