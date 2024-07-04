package store.buzzbook.front.dto.product;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductUpdateRequest {

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
