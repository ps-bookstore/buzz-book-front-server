package store.buzzbook.front.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class OrderFormData {
	private String address;
	private String addressDetail;
	private String addressOption;
	private String addresses;
	private String contactNumber;
	private String email;
	private String name;
	private String price;
	private String receiver;
	private String request;
	private String totalProductPrice;
	private String orderStr;
	private String loginId;
	private String desiredDeliveryDate;
}
