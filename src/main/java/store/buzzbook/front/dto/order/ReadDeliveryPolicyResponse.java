package store.buzzbook.front.dto.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ReadDeliveryPolicyResponse {
	private int id;
	private String name;
	private int standardPrice;
	private int policyPrice;
	private boolean deleted;
}
