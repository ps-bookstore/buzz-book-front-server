package store.buzzbook.front.dto.product;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {
	private int id;
	private String name;
	private CategoryResponse parentCategory;

	public List<String> toList() {
		LinkedList<String> result = new LinkedList<>();
		result.add(name);

		if (parentCategory != null) {
			result.addAll(0, parentCategory.toList());
		}

		return result;
	}
}



