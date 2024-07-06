package store.buzzbook.front.client.order;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.front.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.order.ReadOrderWithoutLoginRequest;
import store.buzzbook.front.dto.order.ReadWrappingResponse;

@FeignClient(name = "orderClient", url = "http://${api.gateway.host}:"
	+ "${api.gateway.port}/api/orders")
public interface OrderClient {

	@PostMapping("non-member")
	ResponseEntity<ReadOrderResponse> getOrderWithoutLogin(
		@RequestBody ReadOrderWithoutLoginRequest readOrderWithoutLoginRequest);

	@GetMapping("/delivery-policy/all")
	ResponseEntity<List<ReadDeliveryPolicyResponse>> getAllDeliveryPolicy();

	@GetMapping("/wrapping/all")
	ResponseEntity<List<ReadWrappingResponse>> getAllWrappings();
}
