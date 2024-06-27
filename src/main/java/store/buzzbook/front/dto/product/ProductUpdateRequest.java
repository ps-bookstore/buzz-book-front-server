package store.buzzbook.front.dto.product;

import lombok.Builder;

@Builder
public class ProductUpdateRequest {
	private int stock;
	private int price;
	private String productName;
	private String thumbnailPath;
	private String stockStatus;
	private int categoryId;
	private String description;

}
