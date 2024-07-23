package store.buzzbook.front.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateWrappingRequest {
	@NotBlank(message = "포장지의 이름은 공백이 불가합니다.")
	private String paper;
	@Min(value = 0, message = "포장지 가격은 0보다 커야 합니다.")
	private int price;
}
