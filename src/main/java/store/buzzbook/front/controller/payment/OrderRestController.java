package store.buzzbook.front.controller.payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.dto.order.CreateOrderDetailRequest;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.OrderFormData;
import store.buzzbook.front.dto.order.ReadOrderResponse;


@RestController
@Slf4j
public class OrderRestController {

	private RestClient restClient;

	@PostMapping(value = "order/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<ReadOrderResponse> register(@RequestBody MultiValueMap<String, String> createOrderRequest) {
		OrderFormData orderFormData = convertMultiValueMapToDTO(createOrderRequest);

		CreateOrderRequest request = new CreateOrderRequest();
		request.setAddress(orderFormData.getAddress());
		request.setAddressDetail(orderFormData.getAddressDetail());
		request.setContactNumber(orderFormData.getContactNumber());
		request.setEmail(orderFormData.getEmail());
		request.setPrice(Integer.parseInt(orderFormData.getPrice().replace(",", "")));
		request.setLoginId(orderFormData.getLoginId());
		request.setReceiver(orderFormData.getReceiver());
		request.setRequest(orderFormData.getRequest());
		request.setOrderStr(orderFormData.getOrderStr());
		request.setDesiredDeliveryDate(orderFormData.getDesiredDeliveryDate());
		request.setDeliveryPolicyId(1);
		request.setOrderStatusId(1);

		request.setZipcode(61459);

		List<CreateOrderDetailRequest> orderDetails = new ArrayList<>();

		for (int i = 0; i < orderFormData.getProductNameList().size(); i++) {
			orderDetails.add(new CreateOrderDetailRequest(Integer.parseInt(orderFormData.getPrice()), Integer.parseInt(orderFormData.getProductQuantityList().get(i)),
				Boolean.getBoolean(orderFormData.getWrapList().get(i)), 1, Integer.parseInt(orderFormData.getWrappingIdList().get(i)), Integer.parseInt(orderFormData.getProductIdList().get(i)),
				orderFormData.getProductNameList().get(i), "", null));
		}

		request.setDetails(orderDetails);

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<CreateOrderRequest> entity = new HttpEntity<>(request, headers);

		ResponseEntity<ReadOrderResponse> response = restTemplate.exchange(
			"http://localhost:8090/api/orders/register", HttpMethod.POST, entity, ReadOrderResponse.class);

		log.warn("register {}", response.getBody().toString());

		return ResponseEntity.ok(response.getBody());

	}

	public OrderFormData convertMultiValueMapToDTO(MultiValueMap<String, String> multiValueMap) {
		OrderFormData dto = new OrderFormData();

		dto.setAddress(getStringValue(multiValueMap, "address"));
		dto.setAddressDetail(getStringValue(multiValueMap, "addressDetail"));
		dto.setAddressOption(getStringValue(multiValueMap, "addressOption"));
		// dto.setAddresses(getStringListValue(multiValueMap, "addresses"));
		dto.setContactNumber(getStringValue(multiValueMap, "contactNumber"));
		dto.setDesiredDeliveryDate(getStringValue(multiValueMap, "deliveryDate"));
		dto.setEmail(getStringValue(multiValueMap, "email"));
		dto.setName(getStringValue(multiValueMap, "name"));
		dto.setPrice(getNumericValue(multiValueMap, "price"));
		dto.setReceiver(getStringValue(multiValueMap, "receiver"));
		dto.setRequest(getStringValue(multiValueMap, "request"));
		dto.setTotalProductPrice(getNumericValue(multiValueMap, "totalProductPrice"));
		dto.setOrderStr(getStringValue(multiValueMap, "orderStr"));
		dto.setLoginId(getStringValue(multiValueMap, "loginId"));

		// Handling dynamic lists
		for (String key : multiValueMap.keySet()) {
			if (key.matches(".*-(\\d+)")) {
				String baseKey = key.substring(0, key.lastIndexOf('-'));
				String indexStr = key.substring(key.lastIndexOf('-') + 1);
				int index = Integer.parseInt(indexStr);

				switch (baseKey) {
					case "productName":
						dto.ensureProductNameListSize(index + 1);
						dto.getProductNameList().set(index, multiValueMap.getFirst(key));
						break;
					case "productPrice":
						dto.ensureProductPriceListSize(index + 1);
						dto.getProductPriceList().set(index, multiValueMap.getFirst(key).replace(",", ""));
						break;
					case "productQuantity":
						dto.ensureProductQuantityListSize(index + 1);
						dto.getProductQuantityList().set(index, multiValueMap.getFirst(key));
						break;
					case "productId":
						dto.ensureProductIdListSize(index + 1);
						dto.getProductIdList().set(index, multiValueMap.getFirst(key));
						break;
					case "packages":
						dto.ensureWrappingIdListSize(index + 1);
						dto.getWrappingIdList().set(index, multiValueMap.getFirst(key));
						break;
					case "packing":
						dto.ensureWrapListSize(index + 1);
						dto.getWrapList().set(index, multiValueMap.getFirst(key));
						break;
					// Add additional cases as needed for other dynamic lists
					default:
						// Handle unknown baseKey if necessary
						break;
				}
			}
		}

		return dto;
	}

	private String getStringValue(MultiValueMap<String, String> multiValueMap, String key) {
		return multiValueMap.containsKey(key) ? multiValueMap.getFirst(key) : null;
	}

	private List<String> getStringListValue(MultiValueMap<String, String> multiValueMap, String key) {
		return multiValueMap.containsKey(key) ? multiValueMap.get(key) : new ArrayList<>();
	}

	private String getNumericValue(MultiValueMap<String, String> multiValueMap, String key) {
		if (multiValueMap.containsKey(key)) {
			String value = multiValueMap.getFirst(key);
			try {
				return value.replace(",", "");
			} catch (NumberFormatException e) {
				log.warn("Invalid numeric value for {}: {}", key, value);
			}
		}
		return null;
	}

}
