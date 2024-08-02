package store.buzzbook.front.dto.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;

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
	private String sender;
	private String receiverContactNumber;
	private String zipcode;
	private String couponCode;
	private String orderEmail;
	private String myPoint;
	private String deliveryRate;
	private String deductedPoints;
	private String earnedPoints;
	private String deductedCouponPrice;
	private List<String> productNameList = new ArrayList<>();
	private List<String> productPriceList = new ArrayList<>();
	private List<String> productQuantityList = new ArrayList<>();
	private List<String> productIdList = new ArrayList<>();
	private List<String> wrappingIdList = new ArrayList<>();
	private List<String> wrapList = new ArrayList<>();

	public void ensureProductNameListSize(int size) {
		ensureListSize(productNameList, size);
	}

	public void ensureProductPriceListSize(int size) {
		ensureListSize(productPriceList, size);
	}

	public void ensureProductQuantityListSize(int size) {
		ensureListSize(productQuantityList, size);
	}

	public void ensureProductIdListSize(int size) {
		ensureListSize(productIdList, size);
	}

	public void ensureWrappingIdListSize(int size) {
		ensureListSize(wrappingIdList, size);
	}

	public void ensureWrapListSize(int size) {
		ensureListSize(wrapList, size);
	}

	private void ensureListSize(List<String> list, int size) {
		while (list.size() < size) {
			list.add(null);
		}
	}
}
