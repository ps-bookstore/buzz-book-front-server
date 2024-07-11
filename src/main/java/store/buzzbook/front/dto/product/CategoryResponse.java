package store.buzzbook.front.dto.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {
	private int id;
	private String name;
	@Nullable
	private Integer parentCategoryId;
	@Nullable
	private String parentCategoryName;
	private List<CategoryResponse> subCategories = new ArrayList<>();

}



