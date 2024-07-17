package store.buzzbook.front.dto.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocumentResponse {
	private int id;
	private String productName;
	private String thumbnailPath;
	private String description;
	private int price;
	private String stockStatus;
	private String category;
}