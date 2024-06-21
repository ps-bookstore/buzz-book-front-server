package store.buzzbook.front.dto.order;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.front.entity.product.Product;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderDetailResponse {
	private long id;
	private int price;
	private int quantity;
	private boolean wrap;
	private ZonedDateTime createdDate;
	private OrderStatusResponse orderStatus;
	private WrappingResponse wrapping;
	private Product product;
}
