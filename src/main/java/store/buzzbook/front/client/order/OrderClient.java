package store.buzzbook.front.client.order;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.front.common.config.FeignConfig;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.front.dto.order.ReadOrderDetailResponse;
import store.buzzbook.front.dto.order.ReadOrderRequest;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.order.ReadOrderStatusResponse;
import store.buzzbook.front.dto.order.ReadOrderWithoutLoginRequest;
import store.buzzbook.front.dto.order.ReadOrdersRequest;
import store.buzzbook.front.dto.order.ReadWrappingResponse;
import store.buzzbook.front.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.front.dto.order.UpdateOrderRequest;

@FeignClient(name = "couponClient", url = "http://${api.gateway.host}:"
	+ "${api.gateway.port}/api/orders", configuration = FeignConfig.class)
public interface OrderClient {
	@PostMapping("/list")
	ResponseEntity<?> getOrders(@RequestBody ReadOrdersRequest readOrdersRequest);

	@PostMapping("/register")
	ResponseEntity<ReadOrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest);

	@PutMapping
	ResponseEntity<ReadOrderResponse> updateOrder(@RequestBody UpdateOrderRequest updateOrderRequest);

	@PutMapping("/detail")
	ResponseEntity<ReadOrderDetailResponse> updateOrderDetail(
		@RequestBody UpdateOrderDetailRequest updateOrderDetailRequest);

	@PostMapping("/id")
	ResponseEntity<ReadOrderResponse> getOrder(@RequestBody ReadOrderRequest readOrderRequest);

	@PostMapping("non-member")
	ResponseEntity<ReadOrderResponse> getOrderWithoutLogin(
		@RequestBody ReadOrderWithoutLoginRequest readOrderWithoutLoginRequest);

	@GetMapping("/status/all")
	ResponseEntity<List<ReadOrderStatusResponse>> getAllOrderStatus();

	@GetMapping("/delivery-policy/all")
	ResponseEntity<List<ReadDeliveryPolicyResponse>> getAllDeliveryPolicy();

	@GetMapping("/wrapping/all")
	ResponseEntity<List<ReadWrappingResponse>> getAllWrappings();
}
