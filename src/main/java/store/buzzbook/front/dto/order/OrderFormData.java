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
	private String deliveryDate;
	private String email;
	private String name;
	private int price;
	private String receiver;
	private String request;
	private int totalProductPrice;
	private String orderStr;
}
