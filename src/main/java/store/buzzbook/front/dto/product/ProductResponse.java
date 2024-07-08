package store.buzzbook.front.dto.product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;

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

}
