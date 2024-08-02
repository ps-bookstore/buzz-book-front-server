package store.buzzbook.front.dto.order;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateOrderDetailRequest {
	private int price;
	private int quantity;
	private boolean wrap;
	private LocalDateTime createAt;
	private String orderStatus;
	private Integer wrappingId;
	private Long orderId;
	private int productId;
	private String productName;
	private String thumbnailPath;

	public CreateOrderDetailRequest(int price, int quantity, boolean wrap, LocalDateTime createAt, String orderStatus,
		Integer wrappingId, Long orderId, int productId, String productName, String thumbnailPath) {
		this.price = price;
		this.quantity = quantity;
		this.wrap = wrap;
		this.createAt = createAt;
		this.orderStatus = orderStatus;
		this.wrappingId = wrappingId;
		this.orderId = orderId;
		this.productId = productId;
		this.productName = productName;
		this.thumbnailPath = thumbnailPath;
	}
}
