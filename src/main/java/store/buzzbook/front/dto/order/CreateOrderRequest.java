package store.buzzbook.front.dto.order;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOrderRequest {
	private int price;
	private String request;
	private String address;
	private String addressDetail;
	private int zipcode;
	private ZonedDateTime desiredDeliveryDate;
	private String receiver;
	private int deliveryPolicyId;
	private String userId;
	private List<CreateOrderDetailRequest> details;
}
