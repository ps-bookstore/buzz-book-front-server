package store.buzzbook.front.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {
	private int id;
	private String name;
	private CategoryResponse parentCategory;

	public String listToString() {
		String result = this.name;

		if (parentCategory != null) {
			result = result + " > " + parentCategory.listToString();
		}

		return result;
	}
}
