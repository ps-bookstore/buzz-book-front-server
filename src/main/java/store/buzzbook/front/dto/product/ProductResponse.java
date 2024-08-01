package store.buzzbook.front.dto.product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductResponse {

	private int id;
	private int stock;
	private String productName;
	private String description;
	private int price;
	private LocalDate forwardDate;
	private int score;
	@Nullable
	private String thumbnailPath;
	private String stockStatus;
	private CategoryResponse category;
	private List<TagResponse> tags = new ArrayList<>();


	public static boolean isPackable(ProductResponse product) {
		return product.getTags().stream().map(TagResponse::getName).toList().contains("포장가능");
	}
}
