package store.buzzbook.front.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class BookApiRequest {
	private List<Item> items;

	@Getter
	public static class Item {
		private String title;
		private String description;
		private String isbn;
		private String author;
		private String publisher;
		private String pubDate;

		@Setter
		private String cover;
		private int customerReviewRank;
		private String categoryName;
		private int pricestandard;
		private int pricesales;
		private String stock;
		private String product;
	}
}