package store.buzzbook.front.dto.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRequest {

	// private int id;
	private	int stock;
	private int price;
	private String forwardDate;
	private int score;
	private String thumbnailPath;
	private int categoryId;
	private String productName;
	private String description;
	private String stockStatus;
}

