package store.buzzbook.front.dto.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
	@NotBlank
	private String name;
	@Nullable @Min(1)
	private Integer parentCategoryId;
	private List<Integer> subCategoryIds = new ArrayList<>();
}
