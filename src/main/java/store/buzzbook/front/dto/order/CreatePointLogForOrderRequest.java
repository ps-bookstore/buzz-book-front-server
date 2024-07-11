package store.buzzbook.front.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreatePointLogForOrderRequest {
	String pointPolicyName;
	String pointOrderInquiry;
	int price;
}
