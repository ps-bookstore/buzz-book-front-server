package store.buzzbook.front.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {

	private int id;
	private String name;
	private CategoryResponse parentCategory;
}