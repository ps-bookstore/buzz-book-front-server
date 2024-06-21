package store.buzzbook.front.dto.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRequest {

	int id;
	int stock;
	int price;
	String forwardDate;
	int score;
	String thumbnailPath;
	int categoryId;
	String productName;
	String stockStatus;
}

