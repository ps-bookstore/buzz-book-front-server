package store.buzzbook.front.dto.product;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateForm {

	private int id;
	private int stock;
	private int price;
	@Nullable
	private String thumbnailPath;
	private int categoryId;
	private String name;
	private String description;
	private String stockStatus;

	public ProductUpdateForm(ProductResponse res) {
		this.id = res.getId();
		this.stock = res.getStock();
		this.price = res.getPrice();
		this.thumbnailPath = res.getThumbnailPath();
		this.categoryId = res.getCategory().getId();
		this.name = res.getProductName();
		this.description = res.getDescription();
		this.stockStatus = res.getStockStatus();
	}

	public static ProductUpdateRequest convertFormToReq(ProductUpdateForm req) {
		return ProductUpdateRequest.builder()
			.stock(req.getStock())
			.price(req.getPrice())
			.thumbnailPath(req.getThumbnailPath())
			.categoryId(req.getCategoryId())
			.productName(req.getName())
			.description(req.getDescription())
			.stockStatus(req.getStockStatus())
			.build();
	}

	public String toString() {
		return id + " " + stock + " " + price + " " + thumbnailPath + " " + categoryId + " " + name + " " + stockStatus;
	}

}
