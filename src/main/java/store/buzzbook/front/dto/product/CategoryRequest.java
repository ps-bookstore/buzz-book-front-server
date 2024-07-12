package store.buzzbook.front.dto.product;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryRequest {
	@NotBlank
	private String name;
	@Nullable
	@Min(1)
	private Integer parentCategoryId;
}
