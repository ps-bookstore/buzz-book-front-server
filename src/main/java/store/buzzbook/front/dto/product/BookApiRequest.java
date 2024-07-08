package store.buzzbook.front.dto.product;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

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
		private String category;
		private int pricestandard;
		private int pricesales;
		private String stock;
		private String product;
	}
}