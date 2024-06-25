package store.buzzbook.front.controller.payment;

import static org.springframework.http.MediaType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.util.ApiUtils;
import store.buzzbook.front.dto.order.CreateOrderDetailRequest;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.OrderFormData;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.payment.PaymentCancelRequest;
import store.buzzbook.front.dto.payment.ReadPaymentResponse;
import store.buzzbook.front.dto.payment.TossPaymentCancelRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Slf4j
public class PaymentRestController {

	private RestClient restClient;
	@PostMapping(value = "order/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<ReadOrderResponse> transferPaymentRequest(@RequestBody MultiValueMap<String, String> createOrderRequest) {
		Map<String, String[]> result = createOrderRequest.entrySet()
			.stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				e -> e.getValue().toArray(new String[e.getValue().size()])
			));

		OrderFormData orderFormData = convertMultiValueMapToDTO(createOrderRequest);

		CreateOrderRequest request = new CreateOrderRequest();
		request.setLoginId("testid123123");
		request.setRequest(orderFormData.getRequest());
		request.setDeliveryPolicyId(1);
		request.setPrice(orderFormData.getPrice());
		// request.setDetails();
		request.setAddress(orderFormData.getAddress());
		request.setAddressDetail(orderFormData.getAddressDetail());
		request.setZipcode(61459);
		request.setOrderStr("fdssfdsfsdfdf");  // 받아오자
		request.setReceiver(orderFormData.getReceiver());

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

		orderDetails.add(new CreateOrderDetailRequest(1, 1, false, 1, null, 1, null));

		request.setDetails(orderDetails);

		//
		// RestTemplate restTemplate = new RestTemplate();
		//
		// HttpHeaders headers = new HttpHeaders();
		// headers.set("Content-Type", "application/json");
		//
		// HttpEntity<CreateOrderRequest> entity = new HttpEntity<>(request, headers);
		//
		// ResponseEntity<ReadOrderResponse> readOrderResponse = restTemplate.exchange(
		// 	"http://localhost:8090/api/orders", HttpMethod.POST, entity, ReadOrderResponse.class);


		ResponseEntity<ReadOrderResponse> readOrderResponse = restClient.post()
			.uri(ApiUtils.getOrderBasePath()+"/register")
			.header(APPLICATION_JSON_VALUE)
			.body(request)
			.retrieve()
			.toEntity(ReadOrderResponse.class);

		log.warn("readOrderResponse: {}", readOrderResponse);

		return readOrderResponse;
	}

	public OrderFormData convertMultiValueMapToDTO(MultiValueMap<String, String> multiValueMap) {
		OrderFormData dto = new OrderFormData();

		if (multiValueMap.containsKey("address")) {
			dto.setAddress(multiValueMap.getFirst("address"));
		}

		if (multiValueMap.containsKey("addressDetail")) {
			dto.setAddressDetail(multiValueMap.getFirst("addressDetail"));
		}

		if (multiValueMap.containsKey("contactNumber")) {
			dto.setContactNumber(multiValueMap.getFirst("contactNumber"));
		}

		if (multiValueMap.containsKey("deliveryDate")) {
			dto.setDeliveryDate(multiValueMap.getFirst("deliveryDate"));
		}

		if (multiValueMap.containsKey("email")) {
			dto.setEmail(multiValueMap.getFirst("email"));
		}

		if (multiValueMap.containsKey("name")) {
			dto.setName(multiValueMap.getFirst("name"));
		}

		if (multiValueMap.containsKey("price")) {
			try {
				dto.setPrice(Integer.parseInt(Objects.requireNonNull(multiValueMap.getFirst("price")).replace(",", "")));
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
				dto.setTotalProductPrice(Integer.parseInt(Objects.requireNonNull(multiValueMap.getFirst("totalProductPrice")).replace(",", "")));
			} catch (NumberFormatException e) {
				log.warn("Invalid total price value: {}", multiValueMap.getFirst("totalProductPrice"));
			}
		}

		return dto;
	}
}
