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
public class CreateDeliveryPolicyRequest {
	@NotBlank(message = "쿠폰 정책의 이름은 공백이 불가합니다.")
	private String name;
	@Min(value = 0, message = "기준 금액은 0보다 커야 합니다.")
	private int standardPrice;
	private int policyPrice;
}
