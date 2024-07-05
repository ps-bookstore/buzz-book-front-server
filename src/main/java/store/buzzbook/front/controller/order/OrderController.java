package store.buzzbook.front.controller.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.order.OrderClient;
import store.buzzbook.front.common.annotation.OrderJwtValidate;
import store.buzzbook.front.common.exception.user.UserTokenException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.order.CreateOrderDetailRequest;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.ReadAllDeliveryPolicyRequest;
import store.buzzbook.front.dto.order.ReadAllWrappingRequest;
import store.buzzbook.front.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.front.dto.order.ReadOrderDetailResponse;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.order.ReadOrderWithoutLoginRequest;
import store.buzzbook.front.dto.order.ReadOrdersRequest;
import store.buzzbook.front.dto.order.ReadWrappingResponse;
import store.buzzbook.front.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.front.dto.user.AddressInfo;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.cart.CartService;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.UserService;

@Controller
@RequiredArgsConstructor
public class OrderController {
	private final UserService userService;
	private final CartService cartService;
	private final CookieUtils cookieUtils;
	private final JwtService jwtService;
	private final OrderClient orderClient;

	@Value("${api.gateway.host}")
	private String host;

	@Value("${api.gateway.port}")
	private int port;

	@OrderJwtValidate
	@GetMapping("/order")
	public String order(Model model, HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(JwtService.USER_ID);
		UserInfo userInfo;

		List<CartDetailResponse> cartDetailResponses = cartService.getCartByRequest(request);
		model.addAttribute("page", "order");
		model.addAttribute("title", "주문하기");

		List<AddressInfo> addressInfos = new ArrayList<>();
		addressInfos.add(AddressInfo.builder().id(1).addressName("우리집").build());
		model.addAttribute("addressInfos", addressInfos);
		CreateOrderRequest orderRequest = new CreateOrderRequest();
		orderRequest.setDeliveryPolicyId(1);

        if (userId != null) {
            userInfo = userService.getUserInfo(userId);
            model.addAttribute("myInfo", userInfo);
            orderRequest.setLoginId(JwtService.LOGIN_ID);
        } else {
            model.addAttribute("myInfo", UserInfo.builder().build());
        }

		List<CreateOrderDetailRequest> details = new ArrayList<>();
		for (CartDetailResponse cartDetail : cartDetailResponses) {
			details.add(new CreateOrderDetailRequest(cartDetail.getPrice(), cartDetail.getQuantity(), false,
				LocalDateTime.now(), 1, 1, null, cartDetail.getProductId(), cartDetail.getProductName(),
				cartDetail.getThumbnailPath(), ""));
		}

		orderRequest.setDetails(details);
		model.addAttribute("createOrderRequest", orderRequest);

		ResponseEntity<List<ReadWrappingResponse>> readWrappingResponse = orderClient.getAllWrappings();

		model.addAttribute("packages", readWrappingResponse.getBody());

		ResponseEntity<List<ReadDeliveryPolicyResponse>> readDeliveryPolicyResponse = orderClient.getAllDeliveryPolicy();

		model.addAttribute("policies", readDeliveryPolicyResponse.getBody());

		return "index";
	}

	@OrderJwtValidate
	@GetMapping("/order/instant")
	public String instantOrder(Model model, HttpServletRequest request) {
		Long userId = (Long)request.getAttribute(JwtService.USER_ID);
		UserInfo userInfo;

		// List<CartDetailResponse> cartDetailResponses = cartService.getCartByRequest(request);

		model.addAttribute("page", "order");
		model.addAttribute("title", "주문하기");

		List<AddressInfo> addressInfos = new ArrayList<>();
		addressInfos.add(AddressInfo.builder().id(1).addressName("우리집").build());
		model.addAttribute("addressInfos", addressInfos);
		CreateOrderRequest orderRequest = new CreateOrderRequest();
		orderRequest.setDeliveryPolicyId(1);

		if (userId != null) {
			userInfo = userService.getUserInfo(userId);
			model.addAttribute("myInfo", userInfo);
			orderRequest.setLoginId(JwtService.LOGIN_ID);
		} else {
			model.addAttribute("myInfo", UserInfo.builder().build());
		}

		// List<CreateOrderDetailRequest> details = new ArrayList<>();
		// for (CartDetailResponse cartDetail : cartDetailResponses) {
		// 	details.add(new CreateOrderDetailRequest(cartDetail.getPrice(), cartDetail.getQuantity(), false,
		// 		LocalDateTime.now(), 1, 1, null, cartDetail.getProductId(), cartDetail.getProductName(),
		// 		cartDetail.getThumbnailPath(), ""));
		// }

		// orderRequest.setDetails(details);
		model.addAttribute("createOrderRequest", orderRequest);

		ResponseEntity<List<ReadWrappingResponse>> readWrappingResponse = orderClient.getAllWrappings();

		model.addAttribute("packages", readWrappingResponse.getBody());

		ResponseEntity<List<ReadDeliveryPolicyResponse>> readDeliveryPolicyResponse = orderClient.getAllDeliveryPolicy();

		model.addAttribute("policies", readDeliveryPolicyResponse.getBody());

		return "index";
	}

	@GetMapping("/my-page")
	public String myPage(Model model, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
 		if (page < 1) {
			page = 1;
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
			return "redirect:/my-page?page=" + (page - 1) + "&size=10";
		}

		model.addAttribute("page", "mypage");

		model.addAttribute("myOrders", response.getBody().get("responseData"));
		model.addAttribute("total", response.getBody().get("total"));
		model.addAttribute("currentPage", page);

		return "index";
	}

	@GetMapping("/nonMemberOrder")
	public String nonMemberOrder(Model model, HttpSession session, @RequestParam("orderId") String orderId,
		@RequestParam("orderPassword") String orderPassword) {

		ReadOrderWithoutLoginRequest request = new ReadOrderWithoutLoginRequest(orderId, orderPassword);

		ResponseEntity<ReadOrderResponse> response = orderClient.getOrderWithoutLogin(request);

		// RestTemplate restTemplate = new RestTemplate();
		//
		// HttpHeaders headers = new HttpHeaders();
		// headers.set("Content-Type", "application/json");
		//
		// HttpEntity<ReadOrderWithoutLoginRequest> readOrderWithoutLoginRequestHttpEntity = new HttpEntity<>(request,
		// 	headers);
		//
		// ResponseEntity<ReadOrderResponse> response = restTemplate.exchange(
		// 	String.format("http://%s:%d/api/orders/non-member", host, port), HttpMethod.POST,
		// 	readOrderWithoutLoginRequestHttpEntity, ReadOrderResponse.class);

		model.addAttribute("page", "nonMemberOrder");

		model.addAttribute("myOrders", response.getBody());

		return "index";
	}

	@GetMapping("/myorderdetail/cancel")
	public String cancelOrderBeforeShipping(HttpSession session, Model model, @RequestParam("id") long orderDetailId,
		@RequestParam int page,
		@RequestParam int size, HttpServletRequest request) throws Exception {

		UpdateOrderDetailRequest updateOrderDetailRequest = UpdateOrderDetailRequest.builder()
			.id(orderDetailId)
			.orderStatusName("CANCELED")
			.build();

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

		HttpEntity<UpdateOrderDetailRequest> updateOrderRequestHttpEntity = new HttpEntity<>(updateOrderDetailRequest, headers);
		ResponseEntity<ReadOrderDetailResponse> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/detail", host, port), HttpMethod.PUT, updateOrderRequestHttpEntity,
			ReadOrderDetailResponse.class);

		return "redirect:/my-page?page=" + page + "&size=10";
	}
}
