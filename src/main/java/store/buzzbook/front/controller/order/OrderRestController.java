package store.buzzbook.front.controller.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.user.UserTokenException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.order.CreateOrderDetailRequest;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.OrderFormData;
import store.buzzbook.front.dto.order.ReadOrderResponse;


@RestController
@Slf4j
@RequiredArgsConstructor
public class OrderRestController {
	private final CookieUtils cookieUtils;

	@PostMapping(value = "order/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<ReadOrderResponse> register(@RequestBody MultiValueMap<String, String> createOrderRequest, HttpServletRequest request) {
		OrderFormData orderFormData = convertMultiValueMapToDTO(createOrderRequest);

		CreateOrderRequest orderRequest = new CreateOrderRequest();
		orderRequest.setAddress(orderFormData.getAddress());
		orderRequest.setAddressDetail(orderFormData.getAddressDetail());
		orderRequest.setContactNumber(orderFormData.getContactNumber());
		orderRequest.setEmail(orderFormData.getEmail());
		orderRequest.setPrice(Integer.parseInt(orderFormData.getPrice().replace(",", "")));
		if (orderFormData.getLoginId() != null) {
			orderRequest.setLoginId(orderFormData.getLoginId());
		} else {
			orderRequest.setLoginId(null);
		}
		orderRequest.setReceiver(orderFormData.getReceiver());
		orderRequest.setRequest(orderFormData.getRequest());
		orderRequest.setOrderStr(orderFormData.getOrderStr());
		orderRequest.setDesiredDeliveryDate(orderFormData.getDesiredDeliveryDate());
		orderRequest.setSender(orderFormData.getSender());
		orderRequest.setReceiverContactNumber(orderFormData.getReceiverContactNumber());
		orderRequest.setDeliveryPolicyId(1);
		orderRequest.setOrderStatusId(1);
		orderRequest.setOrderPassword(orderFormData.getOrderPassword());

		orderRequest.setZipcode(Integer.parseInt(orderFormData.getZipcode()));

		List<CreateOrderDetailRequest> orderDetails = new ArrayList<>();

		for (int i = 0; i < orderFormData.getProductNameList().size()-1; i++) {
			orderDetails.add(new CreateOrderDetailRequest(Integer.parseInt(orderFormData.getProductPriceList().get(i)),
				Integer.parseInt(orderFormData.getProductQuantityList().get(i)),
				orderFormData.getWrapList().get(i).equals("1"), LocalDateTime.now(), 1,
				Integer.parseInt(orderFormData.getWrappingIdList().get(i)),
				null, Integer.parseInt(orderFormData.getProductIdList().get(i)), "",
				orderFormData.getProductNameList().get(i), orderFormData.getCouponCode()));
		}

		orderRequest.setDetails(orderDetails);

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		Optional<Cookie> jwt = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		if(jwt.isEmpty()|| refresh.isEmpty()) {
			throw new UserTokenException();
		}

		String accessToken = String.format("Bearer %s", jwt.get().getValue());
		String refreshToken = String.format("Bearer %s", refresh.get().getValue());

		headers.set(CookieUtils.COOKIE_JWT_ACCESS_KEY, accessToken);
		headers.set(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);

		HttpEntity<CreateOrderRequest> createOrderRequestHttpEntity = new HttpEntity<>(orderRequest, headers);

		ResponseEntity<ReadOrderResponse> response = restTemplate.exchange(
			"http://localhost:8090/api/orders/register", HttpMethod.POST, createOrderRequestHttpEntity, ReadOrderResponse.class);

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
		dto.setSender(getStringValue(multiValueMap, "sender"));
		dto.setReceiverContactNumber(getStringValue(multiValueMap, "receiverContactNumber"));
		dto.setZipcode(getStringValue(multiValueMap, "zipcode"));
		dto.setCouponCode(getStringValue(multiValueMap, "couponCode"));
		dto.setOrderPassword(getStringValue(multiValueMap, "orderPassword"));
		for (String key : multiValueMap.keySet()) {
			if (key.matches(".*-(\\d+)")) {
				String baseKey = key.substring(0, key.lastIndexOf('-'));
				String indexStr = key.substring(key.lastIndexOf('-') + 1);
				int index = Integer.parseInt(indexStr);

				switch (baseKey) {
					case "dataName":
						dto.ensureProductNameListSize(index + 1);
						dto.getProductNameList().add(index, multiValueMap.getFirst(key));
						break;
					case "dataPrice":
						dto.ensureProductPriceListSize(index + 1);
						dto.getProductPriceList().add(index, multiValueMap.getFirst(key).replace(",", ""));
						break;
					case "dataQuantity":
						dto.ensureProductQuantityListSize(index + 1);
						dto.getProductQuantityList().add(index, multiValueMap.getFirst(key));
						break;
					case "productId":
						dto.ensureProductIdListSize(index + 1);
						dto.getProductIdList().add(index, multiValueMap.getFirst(key));
						break;
					case "packages":
						dto.ensureWrappingIdListSize(index + 1);
						dto.getWrappingIdList().add(index, multiValueMap.getFirst(key));
						break;
					case "packing":
						dto.ensureWrapListSize(index + 1);
						dto.getWrapList().add(index, multiValueMap.getFirst(key));
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
