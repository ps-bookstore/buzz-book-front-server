package store.buzzbook.front.dto.order;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import store.buzzbook.front.dto.product.ProductResponse;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ReadOrderDetailResponse {
	private long id;
	private int price;
	private int quantity;
	private boolean wrap;
	private ZonedDateTime createdDate;
	private ReadOrderStatusResponse readOrderStatusResponse;
	private ReadWrappingResponse wrapping;
	private ProductResponse productResponse;
}
