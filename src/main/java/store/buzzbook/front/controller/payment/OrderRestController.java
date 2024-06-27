package store.buzzbook.front.controller.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
		// Map<String, String[]> result = createOrderRequest.entrySet()
		// 	.stream()
		// 	.collect(Collectors.toMap(
		// 		Map.Entry::getKey,
		// 		e -> e.getValue().toArray(new String[e.getValue().size()])
		// 	));

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

		// request.setDetails();

		request.setZipcode(61459);



		log.warn("Transfer order request: {}", createOrderRequest.entrySet());
		// CreateOrderRequest createOrderRequest = new CreateOrderRequest();
		// createOrderRequest.setOrderStr(orderStr);
		// createOrderRequest.setPrice(price);
		// createOrderRequest.setRequest(request);
		// createOrderRequest.setAddress(address);
		// createOrderRequest.setAddressDetail(addressDetail);
		// createOrderRequest.setZipcode(zipcode);
		// createOrderRequest.setReceiver(receiver);
		// createOrderRequest.setDeliveryPolicyId(deliveryPolicyId);
		// createOrderRequest.setLoginId(loginId);
		//
		// details.put("price", 1000);
		// details.put("quantity", 1);
		// details.put("wrap", false);
		// details.put("orderStatusId", 1);
		// details.put("wrappingId", null);
		// details.put("productId", 1);
		// details.put("orderId", null);
		//
		List<CreateOrderDetailRequest> orderDetails = new ArrayList<>();
		// orderDetails.add(new CreateOrderDetailRequest((Integer)details.get("price"),
		// 	(Integer)details.get("quantity"),
		// 	(boolean)details.get("wrap"),
		// 	(Integer)details.get("orderDetailsId"),
		// 	(Integer)details.get("wrappingId"),
		// 	(Integer)details.get("productId"),
		// 	null));

		orderDetails.add(new CreateOrderDetailRequest(1000, 1, false, 1, null, 1, "name", "path", null));

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

		if (multiValueMap.containsKey("address")) {
			dto.setAddress(multiValueMap.getFirst("address"));
		}

		if (multiValueMap.containsKey("addressDetail")) {
			dto.setAddressDetail(multiValueMap.getFirst("addressDetail"));
		}

		if (multiValueMap.containsKey("addressOption")) {
			dto.setAddressOption(multiValueMap.getFirst("addressOption"));
		}

		if (multiValueMap.containsKey("addresses")) {
			dto.setAddresses(multiValueMap.getFirst("addresses"));
		}

		if (multiValueMap.containsKey("contactNumber")) {
			dto.setContactNumber(multiValueMap.getFirst("contactNumber"));
		}

		if (multiValueMap.containsKey("deliveryDate")) {
			dto.setDesiredDeliveryDate(multiValueMap.getFirst("deliveryDate"));
		}

		if (multiValueMap.containsKey("email")) {
			dto.setEmail(multiValueMap.getFirst("email"));
		}

		if (multiValueMap.containsKey("name")) {
			dto.setName(multiValueMap.getFirst("name"));
		}

		if (multiValueMap.containsKey("price")) {
			try {
				dto.setPrice(Objects.requireNonNull(multiValueMap.getFirst("price")).replace(",", ""));
			} catch (NumberFormatException e) {
				log.warn("Invalid price value: {}", multiValueMap.getFirst("price"));
			}
		}

		if (multiValueMap.containsKey("receiver")) {
			dto.setReceiver(multiValueMap.getFirst("receiver"));
		}

		if (multiValueMap.containsKey("request")) {
			dto.setRequest(multiValueMap.getFirst("request"));
		}

		if (multiValueMap.containsKey("totalProductPrice")) {
			try {
				dto.setTotalProductPrice(
					Objects.requireNonNull(multiValueMap.getFirst("totalProductPrice")).replace(",", ""));
			} catch (NumberFormatException e) {
				log.warn("Invalid total price value: {}", multiValueMap.getFirst("totalProductPrice"));
			}
		}

		if (multiValueMap.containsKey("orderStr")) {
			dto.setOrderStr(multiValueMap.getFirst("orderStr"));
		}

		if (multiValueMap.containsKey("loginId")) {
			dto.setLoginId(multiValueMap.getFirst("loginId"));
		}

		return dto;
	}
}
