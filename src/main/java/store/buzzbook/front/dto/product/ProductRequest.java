package store.buzzbook.front.dto.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRequest {

	private int stock;
	private String productName;
	private String description;
	private int price;
	private String forwardDate;
	private String thumbnailPath;
	private String stockStatus;
	private int categoryId;
}

