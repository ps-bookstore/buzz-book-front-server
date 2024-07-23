package store.buzzbook.front.controller.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.product.review.ReviewClient;
import store.buzzbook.front.common.annotation.OrderJwtValidate;
import store.buzzbook.front.common.exception.user.UserTokenException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.coupon.OrderCouponDetailResponse;
import store.buzzbook.front.dto.order.CreateOrderDetailRequest;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.NonMemberOrderForm;
import store.buzzbook.front.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.order.ReadOrderWithoutLoginRequest;
import store.buzzbook.front.dto.order.ReadOrdersRequest;
import store.buzzbook.front.dto.order.ReadWrappingResponse;
import store.buzzbook.front.dto.product.ProductResponse;
import store.buzzbook.front.dto.user.AddressInfo;
import store.buzzbook.front.dto.user.AddressInfoResponse;
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

	@Value("${api.gateway.host}")
	private String host;

	@Value("${api.gateway.port}")
	private int port;

	@OrderJwtValidate
	@GetMapping("/order")
	public String order(Model model, HttpServletRequest request, HttpServletResponse httpResponse) {
		Optional<Cookie> authorizationHeader =cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refreshHeader =cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		Long userId = null;
		if (authorizationHeader.isPresent() || refreshHeader.isPresent()) {
			userId = jwtService.getUserIdFromJwt(request, httpResponse);
		}

		UserInfo userInfo = null;
		List<AddressInfoResponse> addressInfos = new ArrayList<>();
		Integer myPoint = null;

		List<CartDetailResponse> cartDetailResponses = cartService.getCartByRequest(request);
		model.addAttribute("page", "order");
		model.addAttribute("title", "주문하기");

		List<OrderCouponDetailResponse> myCoupons = new ArrayList<>();

		if (userId != null) {
			userInfo = userService.getUserInfo(userId);

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");

			Optional<Cookie> jwt = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
			Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

			if (jwt.isEmpty() || refresh.isEmpty()) {
				throw new UserTokenException();
			}

			String accessToken = String.format("Bearer %s", jwt.get().getValue());
			String refreshToken = String.format("Bearer %s", refresh.get().getValue());

			headers.set(CookieUtils.COOKIE_JWT_ACCESS_KEY, accessToken);
			headers.set(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);

			HttpEntity<Object> readAddressInfosHttpEntity = new HttpEntity<>(headers);

			ResponseEntity<List<AddressInfoResponse>> response = restTemplate.exchange(
				String.format("http://%s:%d/api/account/address/order", host, port), HttpMethod.GET,
				readAddressInfosHttpEntity,
				new ParameterizedTypeReference<List<AddressInfoResponse>>() {
				});

			addressInfos = response.getBody();

			HttpEntity<Object> readPointHttpEntity = new HttpEntity<>(headers);

			ResponseEntity<Integer> pointResponse = restTemplate.exchange(
				String.format("http://%s:%d/api/account/points/logs/last-point", host, port),
				HttpMethod.GET, readPointHttpEntity, Integer.class);

			myPoint = pointResponse.getBody();

			HttpEntity<List<CartDetailResponse>> cartDetailResponsesHttpEntity = new HttpEntity<>(cartDetailResponses,
				headers);

			ResponseEntity<List<OrderCouponDetailResponse>> orderCouponDetailResponse = restTemplate.exchange(
				String.format("http://%s:%d/api/account/coupons/order", host, port),
				HttpMethod.POST, cartDetailResponsesHttpEntity,
				new ParameterizedTypeReference<List<OrderCouponDetailResponse>>() {
				});

			myCoupons = orderCouponDetailResponse.getBody();
		}

		if (addressInfos != null && !addressInfos.isEmpty()) {
			model.addAttribute("addressInfos", addressInfos);
		} else {
			addressInfos.add(AddressInfoResponse.builder().build());
		}

		model.addAttribute("addressInfos", addressInfos);
		CreateOrderRequest orderRequest = new CreateOrderRequest();
		orderRequest.setDeliveryPolicyId(1);

		if (userId == null) {
			model.addAttribute("myInfo", UserInfo.builder().build());
			myPoint = 0;
		}

		if (userInfo != null) {
			model.addAttribute("myInfo", userInfo);

			orderRequest.setLoginId((String)request.getAttribute(JwtService.LOGIN_ID));
		}

		List<CreateOrderDetailRequest> details = new ArrayList<>();
		for (CartDetailResponse cartDetail : cartDetailResponses) {
			details.add(new CreateOrderDetailRequest(cartDetail.getPrice(), cartDetail.getQuantity(), cartDetail.isCanWrap(),
				LocalDateTime.now(), 1, 1, null, cartDetail.getProductId(), cartDetail.getProductName(),
				cartDetail.getThumbnailPath()));
		}

		orderRequest.setDetails(details);
		model.addAttribute("createOrderRequest", orderRequest);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<List<ReadWrappingResponse>> readWrappingResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/wrapping/all", host, port),
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<ReadWrappingResponse>>() {
			}
		);

		model.addAttribute("packages", readWrappingResponse.getBody());

		ResponseEntity<List<ReadDeliveryPolicyResponse>> readDeliveryPolicyResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/delivery-policy/all", host, port),
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<ReadDeliveryPolicyResponse>>() {
			}
		);

		model.addAttribute("policies", readDeliveryPolicyResponse.getBody());

		model.addAttribute("myPoint", myPoint);

		model.addAttribute("myCoupons", myCoupons);

		return "index";
	}

	@OrderJwtValidate
	@GetMapping("/order/direct/{id}")
	public String instantOrder(Model model, HttpServletRequest request, HttpServletResponse httpResponse, @PathVariable int id) {
		Optional<Cookie> authorizationHeader =cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> refreshHeader =cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

		Long userId = null;
		if (authorizationHeader.isPresent() || refreshHeader.isPresent()) {
			userId = jwtService.getUserIdFromJwt(request, httpResponse);
		}

		UserInfo userInfo = null;
		List<AddressInfoResponse> addressInfos = new ArrayList<>();
		Integer myPoint = null;

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<Integer> readProductHttpEntity = new HttpEntity<>(id, headers);

		ResponseEntity<ProductResponse> productResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/products/%d", host, port, id),
			HttpMethod.GET, readProductHttpEntity, ProductResponse.class);

		model.addAttribute("page", "order");
		model.addAttribute("title", "주문하기");

		List<OrderCouponDetailResponse> myCoupons = new ArrayList<>();

		if (userId != null) {
			userInfo = userService.getUserInfo(userId);

			Optional<Cookie> jwt = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
			Optional<Cookie> refresh = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

			if (jwt.isEmpty() || refresh.isEmpty()) {
				throw new UserTokenException();
			}

			String accessToken = String.format("Bearer %s", jwt.get().getValue());
			String refreshToken = String.format("Bearer %s", refresh.get().getValue());

			headers.set(CookieUtils.COOKIE_JWT_ACCESS_KEY, accessToken);
			headers.set(CookieUtils.COOKIE_JWT_REFRESH_KEY, refreshToken);

			HttpEntity<Object> readAddressInfosHttpEntity = new HttpEntity<>(headers);

			ResponseEntity<List<AddressInfoResponse>> response = restTemplate.exchange(
				String.format("http://%s:%d/api/account/address/order", host, port), HttpMethod.GET,
				readAddressInfosHttpEntity,
				new ParameterizedTypeReference<List<AddressInfoResponse>>() {
				});

			addressInfos = response.getBody();

			HttpEntity<Object> readPointHttpEntity = new HttpEntity<>(headers);

			ResponseEntity<Integer> pointResponse = restTemplate.exchange(
				String.format("http://%s:%d/api/account/points/logs/last-point", host, port),
				HttpMethod.GET, readPointHttpEntity, Integer.class);

			myPoint = pointResponse.getBody();

			List<CartDetailResponse> cartDetails = new ArrayList<>();
			cartDetails.add(CartDetailResponse.builder().price(productResponse.getBody().getPrice()).canWrap(productResponse.getBody().getTags().stream()
					.anyMatch(t -> t.getName().equals("포장가능"))).categoryId(productResponse.getBody().getCategory().getId()).productId(productResponse.getBody().getId())
				.thumbnailPath(productResponse.getBody().getThumbnailPath()).quantity(1).productName(productResponse.getBody().getProductName()).build());

			HttpEntity<List<CartDetailResponse>> cartDetailResponsesHttpEntity = new HttpEntity<>(cartDetails,
				headers);

			ResponseEntity<List<OrderCouponDetailResponse>> orderCouponDetailResponse = restTemplate.exchange(
				String.format("http://%s:%d/api/account/coupons/order", host, port),
				HttpMethod.POST, cartDetailResponsesHttpEntity,
				new ParameterizedTypeReference<List<OrderCouponDetailResponse>>() {
				});

			myCoupons = orderCouponDetailResponse.getBody();
		}

		if (addressInfos != null && !addressInfos.isEmpty()) {
			model.addAttribute("addressInfos", addressInfos);
		} else {
			addressInfos.add(AddressInfoResponse.builder().build());
		}

		model.addAttribute("addressInfos", addressInfos);
		CreateOrderRequest orderRequest = new CreateOrderRequest();
		orderRequest.setDeliveryPolicyId(1);

		if (userId == null) {
			model.addAttribute("myInfo", UserInfo.builder().build());
			myPoint = 0;
		}

		if (userInfo != null) {
			model.addAttribute("myInfo", userInfo);

			orderRequest.setLoginId((String)request.getAttribute(JwtService.LOGIN_ID));
		}

		List<CartDetailResponse> cartDetails = new ArrayList<>();
		cartDetails.add(CartDetailResponse.builder().price(productResponse.getBody().getPrice()).canWrap(productResponse.getBody().getTags().stream()
				.anyMatch(t -> t.getName().equals("포장가능"))).categoryId(productResponse.getBody().getCategory().getId()).productId(productResponse.getBody().getId())
			.thumbnailPath(productResponse.getBody().getThumbnailPath()).quantity(1).productName(productResponse.getBody().getProductName()).build());

		List<CreateOrderDetailRequest> details = new ArrayList<>();
		for (CartDetailResponse cartDetail : cartDetails) {
			details.add(new CreateOrderDetailRequest(cartDetail.getPrice(), cartDetail.getQuantity(), cartDetail.isCanWrap(),
				LocalDateTime.now(), 1, 1, null, cartDetail.getProductId(), cartDetail.getProductName(),
				cartDetail.getThumbnailPath()));
		}

		orderRequest.setDetails(details);
		model.addAttribute("createOrderRequest", orderRequest);

		ResponseEntity<List<ReadWrappingResponse>> readWrappingResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/wrapping/all", host, port),
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<ReadWrappingResponse>>() {
			}
		);

		model.addAttribute("packages", readWrappingResponse.getBody());

		ResponseEntity<List<ReadDeliveryPolicyResponse>> readDeliveryPolicyResponse = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/delivery-policy/all", host, port),
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<ReadDeliveryPolicyResponse>>() {
			}
		);

		model.addAttribute("policies", readDeliveryPolicyResponse.getBody());

		model.addAttribute("myPoint", myPoint);

		model.addAttribute("myCoupons", myCoupons);

		return "index";
	}

	@OrderJwtValidate
	@GetMapping("/orders")
	public String myPageOrders(Model model, @RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "size", defaultValue = "10") Integer size,
		HttpServletRequest request) {
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

		if (jwt.isEmpty() || refresh.isEmpty()) {
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

		if (response.getBody().get("total").toString().equals("0") && page != 1) {
			return "redirect:/orders?page=" + (page - 1) + "&size=10";
		}

		model.addAttribute("page", "mypage-index");
		model.addAttribute("fragment", "mypage-orders");

		model.addAttribute("myOrders", response.getBody().get("responseData"));
		model.addAttribute("total", response.getBody().get("total"));
		model.addAttribute("currentPage", page);
		model.addAttribute("title", "내 주문 목록");

		return "index";
	}

	@GetMapping("/nonMemberOrder")
	public String nonMemberOrderForm(Model model) {
		model.addAttribute("nonMemberOrderForm", new NonMemberOrderForm());
		model.addAttribute("page", "nonMemberOrderForm");
		model.addAttribute("title", "비회원 주문 조회");

		return "index";
	}

	@PostMapping("/nonMemberOrder")
	public String nonMemberOrder(Model model, @ModelAttribute("nonMemberOrderForm") NonMemberOrderForm nonMemberOrderForm) {

		ReadOrderWithoutLoginRequest request = new ReadOrderWithoutLoginRequest(nonMemberOrderForm.getOrderId(),
			nonMemberOrderForm.getOrderEmail());

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<ReadOrderWithoutLoginRequest> readOrderWithoutLoginRequestHttpEntity = new HttpEntity<>(request,
			headers);

		ResponseEntity<ReadOrderResponse> response = restTemplate.exchange(
			String.format("http://%s:%d/api/orders/non-member", host, port), HttpMethod.POST,
			readOrderWithoutLoginRequestHttpEntity, ReadOrderResponse.class);

		model.addAttribute("page", "nonMemberOrder");

		model.addAttribute("myOrder", response.getBody());
		model.addAttribute("title", "비회원 주문");

		return "index";
	}
}
