package store.buzzbook.front.dto.product;

import lombok.Data;

@Data
public class ProductResponse {

	private int id;
	private int stock;
	private int price;
	private String forwardDate;
	int score;
	String thumbnailPath;
	int categoryId;
	String productName;
	String stockStatus;
	private CategoryResponse category;

	@Data
	public static class CategoryResponse {
		private int id;
		private String name;
		private CategoryResponse parentCategory;
	}
}
