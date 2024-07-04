package store.buzzbook.front.dto.product;

import org.springframework.lang.Nullable;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductUpdateRequest {

	private	int stock;
	private int price;
	@Nullable
	private String thumbnailPath;
	private int categoryId;
	private String productName;
	private String description;
	private String stockStatus;
}
