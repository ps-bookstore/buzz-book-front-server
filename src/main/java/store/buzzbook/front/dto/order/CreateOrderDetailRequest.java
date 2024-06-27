package store.buzzbook.front.dto.order;

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
	private boolean wrap = true;
	private int orderStatusId;
	private Integer wrappingId;
	private int productId;
	private String productName;
	private String thumbnailPath;
	private Long orderId;
}
