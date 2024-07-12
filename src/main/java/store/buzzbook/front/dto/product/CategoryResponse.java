package store.buzzbook.front.dto.product;

import java.util.LinkedHashMap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {
	@NotNull
	private int id;
	@NotBlank
	private String name;
	private LinkedHashMap<Integer, String> parentCategory;
	private LinkedHashMap<Integer, String> subCategory;

}
