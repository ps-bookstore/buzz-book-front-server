package store.buzzbook.front.dto.product;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CategoryRequest {
	@NotBlank
	private String name;
	@Nullable
	private Integer parentCategoryId;
}
