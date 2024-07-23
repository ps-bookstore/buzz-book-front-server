package store.buzzbook.front.controller.admin.order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.annotation.OrderAdminJwtValidate;
import store.buzzbook.front.common.exception.user.UserTokenException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.front.dto.order.CreateWrappingRequest;
import store.buzzbook.front.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.front.dto.order.ReadOrderDetailResponse;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.order.ReadOrdersRequest;
import store.buzzbook.front.dto.order.ReadWrappingResponse;
import store.buzzbook.front.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.front.dto.order.UpdateOrderRequest;
import store.buzzbook.front.dto.payment.ReadBillLogRequest;
import store.buzzbook.front.dto.payment.ReadBillLogWithoutOrderResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {
	private final CookieUtils cookieUtils;
	@Value("${api.gateway.host}")
	private String host;

	@Value("${api.gateway.port}")
	private int port;

	@OrderAdminJwtValidate
	@GetMapping
	public String adminOrderPage(Model model, @RequestParam(name = "page", defaultValue = "1") Integer page,
		@RequestParam(name = "size", defaultValue = "10") Integer size, HttpServletRequest request) {
		if (page < 1) {
			page = 1;
		}
		if (size < 1) {
			size = 10;
		}
		ReadOrdersRequest orderRequest = new ReadOrdersRequest();
		orderRequest.setPage(page);
		orderRequest.setSize(size);

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

		HttpEntity<ReadOrdersRequest> readOrderRequestHttpEntity = new HttpEntity<>(orderRequest, headers);

		ResponseEntity<Map> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/list", host, port), HttpMethod.POST, readOrderRequestHttpEntity,
			Map.class);

		if (response.getBody().get("total").toString().equals("0")) {
			return "redirect:/admin/orders?page=" + (page - 1) + "&size=10";
		}

		model.addAttribute("page", "order-manage");

		model.addAttribute("myOrders", response.getBody().get("responseData"));
		model.addAttribute("total", response.getBody().get("total"));
		model.addAttribute("currentPage", page);
		model.addAttribute("title", "주문관리자페이지");

		return "admin/index";
	}

	@OrderAdminJwtValidate
	@GetMapping("/{orderId}")
	public String updateStatus(Model model, @PathVariable String orderId, @RequestParam String status,
		@RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "size", defaultValue = "10") Integer size, HttpServletRequest request) {
		if (page < 1) {
			page = 1;
		}
		if (size < 1) {
			size = 10;
		}

		UpdateOrderRequest updateOrderRequest = UpdateOrderRequest.builder().orderStatusName(status).orderId(orderId).build();

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

		HttpEntity<UpdateOrderRequest> updateOrderRequestHttpEntity = new HttpEntity<>(updateOrderRequest, headers);

		ResponseEntity<ReadOrderResponse> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders", host, port), HttpMethod.PUT, updateOrderRequestHttpEntity, ReadOrderResponse.class);


		ReadOrdersRequest orderRequest = new ReadOrdersRequest();
		orderRequest.setPage(page);
		orderRequest.setSize(size);

		HttpEntity<ReadOrdersRequest> readOrderRequestHttpEntity = new HttpEntity<>(orderRequest, headers);

		ResponseEntity<Map> readResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/list", host, port), HttpMethod.POST, readOrderRequestHttpEntity, Map.class);

		if (readResponse.getBody().get("total").toString().equals("0")){
			return "redirect:/admin/orders?page=" + (page-1) +"&size=10";
		}

		model.addAttribute("myOrders", readResponse.getBody().get("responseData"));
		model.addAttribute("total", readResponse.getBody().get("total"));
		model.addAttribute("currentPage", page);

		model.addAttribute("page", "order-manage");

		return "redirect:/admin/orders?page=" + page +"&size=10";
	}

	@OrderAdminJwtValidate
	@GetMapping("detail/{id}")
	public String updateDetailStatus(Model model, @PathVariable int id, @RequestParam(name = "page", defaultValue = "1") Integer page,
		@RequestParam(name = "size", defaultValue = "10") Integer size, @RequestParam String status, HttpServletRequest request) {
		if (page < 1) {
			page = 1;
		}
		if (size < 1) {
			size = 10;
		}

		UpdateOrderDetailRequest updateOrderDetailRequest = UpdateOrderDetailRequest.builder().orderStatusName(status).id(id).build();

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

		HttpEntity<UpdateOrderDetailRequest> updateOrderDetailRequestHttpEntity = new HttpEntity<>(updateOrderDetailRequest, headers);

		ResponseEntity<ReadOrderDetailResponse> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/detail", host, port), HttpMethod.PUT, updateOrderDetailRequestHttpEntity, ReadOrderDetailResponse.class);

		ReadOrdersRequest orderRequest = new ReadOrdersRequest();
		orderRequest.setPage(page);
		orderRequest.setSize(size);

		HttpEntity<ReadOrdersRequest> readOrderRequestHttpEntity = new HttpEntity<>(orderRequest, headers);

		ResponseEntity<Map> readResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/list", host, port), HttpMethod.POST, readOrderRequestHttpEntity, Map.class);

		if (readResponse.getBody().get("total").toString().equals("0") && page != 1){
			return "redirect:/admin/orders?page=" + (page-1) +"&size=10";
		}

		model.addAttribute("myOrders", readResponse.getBody().get("responseData"));
		model.addAttribute("total", readResponse.getBody().get("total"));
		model.addAttribute("currentPage", page);

		model.addAttribute("page", "order-manage");

		return "redirect:/admin/orders?page=" + page +"&size=10";
	}

	@OrderAdminJwtValidate
	@GetMapping("/billlog")
	public String adminBillLog(Model model, @RequestParam String orderId, HttpServletRequest request) {

		ReadBillLogRequest readBillLogRequest = new ReadBillLogRequest(orderId);

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

		HttpEntity<ReadBillLogRequest> readBillLogRequestHttpEntity = new HttpEntity<>(readBillLogRequest, headers);

		ResponseEntity<List<ReadBillLogWithoutOrderResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/payments/bill-logs", host, port), HttpMethod.POST, readBillLogRequestHttpEntity, new ParameterizedTypeReference<List<ReadBillLogWithoutOrderResponse>>() {});

		model.addAttribute("adminBillLogs", response.getBody());
		model.addAttribute("page", "admin-payment");

		return "admin/index";
	}

	@OrderAdminJwtValidate
	@GetMapping("/delivery-policies")
	public String deliveryPolicies(Model model) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<List<ReadDeliveryPolicyResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/delivery-policy/all", host, port), HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<ReadDeliveryPolicyResponse>>() {}
		);

		model.addAttribute("policies", response.getBody());
		model.addAttribute("title", "배송비 정책 관리");
		model.addAttribute("page", "admin-delivery-policy");

		return "admin/index";
	}

	@OrderAdminJwtValidate
	@GetMapping("/delivery-policies/register")
	public String deliveryPoliciesRegister(Model model) {

		model.addAttribute("title", "배송비 정책 관리");
		model.addAttribute("page", "admin-delivery-policy-register");

		return "admin/index";
	}

	@OrderAdminJwtValidate
	@PostMapping("/delivery-policies/register")
	public String createDeliveryPolicies(Model model, @Valid @ModelAttribute CreateDeliveryPolicyRequest createDeliveryPolicyRequest,
		HttpServletRequest request, BindingResult bindingResult) throws BadRequestException {

		if (bindingResult.hasErrors()) {
			throw new BadRequestException();
		}

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

		HttpEntity<CreateDeliveryPolicyRequest> createDeliveryPolicyRequestHttpEntity = new HttpEntity<>(createDeliveryPolicyRequest, headers);

		ResponseEntity<ReadDeliveryPolicyResponse> readDeliveryPolicyResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/delivery-policy", host, port), HttpMethod.POST, createDeliveryPolicyRequestHttpEntity, ReadDeliveryPolicyResponse.class
		);

		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<List<ReadDeliveryPolicyResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/delivery-policy/all", host, port), HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<ReadDeliveryPolicyResponse>>() {}
		);

		model.addAttribute("policies", response.getBody());
		model.addAttribute("title", "배송비 정책 관리");
		model.addAttribute("page", "admin-delivery-policy");

		return "admin/index";
	}

	@OrderAdminJwtValidate
	@GetMapping("/delivery-policies/delete")
	public String deleteDeliveryPolicies(Model model, @RequestParam int deliveryPolicyId, HttpServletRequest request) {

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

		HttpEntity<Object> deleteDeliveryPolicyRequestHttpEntity = new HttpEntity<>(headers);

		ResponseEntity<String> deleteDeliveryPolicyResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/delivery-policy/%d", host, port, deliveryPolicyId), HttpMethod.DELETE, deleteDeliveryPolicyRequestHttpEntity, String.class
		);

		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<List<ReadDeliveryPolicyResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/delivery-policy/all", host, port), HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<ReadDeliveryPolicyResponse>>() {}
		);

		model.addAttribute("policies", response.getBody());
		model.addAttribute("title", "배송비 정책 관리");
		model.addAttribute("page", "admin-delivery-policy");

		return "admin/index";
	}

	@OrderAdminJwtValidate
	@GetMapping("/wrappings")
	public String wrappings(Model model) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<List<ReadWrappingResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/wrapping/all", host, port), HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<ReadWrappingResponse>>() {}
		);

		model.addAttribute("wrappings", response.getBody());
		model.addAttribute("title", "포장지 관리");
		model.addAttribute("page", "admin-wrapping");

		return "admin/index";
	}

	@OrderAdminJwtValidate
	@GetMapping("/wrappings/register")
	public String wrappingsRegister(Model model) {

		model.addAttribute("title", "포장지 관리");
		model.addAttribute("page", "admin-wrapping-register");

		return "admin/index";
	}

	@OrderAdminJwtValidate
	@PostMapping("/wrappings/register")
	public String createWrappings(Model model, @Valid @ModelAttribute CreateWrappingRequest createWrappingRequest,
		HttpServletRequest request, BindingResult bindingResult) throws BadRequestException {

		if (bindingResult.hasErrors()) {
			throw new BadRequestException();
		}

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

		HttpEntity<CreateWrappingRequest> createWrappingRequestHttpEntity = new HttpEntity<>(createWrappingRequest, headers);

		ResponseEntity<ReadWrappingResponse> readWrappingResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/wrapping", host, port), HttpMethod.POST, createWrappingRequestHttpEntity, ReadWrappingResponse.class
		);

		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<List<ReadWrappingResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/wrapping/all", host, port), HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<ReadWrappingResponse>>() {}
		);

		model.addAttribute("wrappings", response.getBody());
		model.addAttribute("title", "포장지 관리");
		model.addAttribute("page", "admin-wrapping");

		return "admin/index";
	}

	@OrderAdminJwtValidate
	@GetMapping("/wrappings/delete")
	public String deleteWrappings(Model model, @RequestParam int wrappingId, HttpServletRequest request) {

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

		HttpEntity<Object> deleteWrappingRequestHttpEntity = new HttpEntity<>(headers);

		ResponseEntity<String> deleteWrappingResponseResponseEntity = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/wrapping/%d", host, port, wrappingId), HttpMethod.DELETE, deleteWrappingRequestHttpEntity, String.class
		);

		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<List<ReadWrappingResponse>> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/wrapping/all", host, port), HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<ReadWrappingResponse>>() {}
		);

		model.addAttribute("wrappings", response.getBody());
		model.addAttribute("title", "포장지 관리");
		model.addAttribute("page", "admin-wrapping");

		return "admin/index";
	}
}
