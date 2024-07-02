package store.buzzbook.front.client.cart;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.common.config.FeignConfig;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.CreateCartDetailRequest;

@FeignClient(name = "cartClient",url = "http://${api.core.host}:" + "${api.core.port}/api/cart", configuration = FeignConfig.class)
public interface CartClient {
	@GetMapping
	ResponseEntity<List<CartDetailResponse>> getCartByUuid(@RequestParam("uuid") String uuid);

	@DeleteMapping
	ResponseEntity<Void> deleteAllCartDetail(@PathVariable("uuid") String uuid);

	@GetMapping("/guest")
	ResponseEntity<String> createCart();

	@PostMapping("/detail")
	ResponseEntity<Void> createCartDetail(@RequestParam String uuid, @RequestBody CreateCartDetailRequest createCartDetailRequest);

	@DeleteMapping("/detail/{detailId}")
	ResponseEntity<List<CartDetailResponse>> deleteCartDetail(@RequestParam("uuid") String uuid, @PathVariable("detailId") Long detailId);

	@PutMapping("/detail/{detailId}")
	ResponseEntity<Void> updateCartDetail(@RequestParam String uuid,@PathVariable Long detailId, @RequestParam Integer quantity);

	@GetMapping("/uuid")
	ResponseEntity<String> getUuidByUserId();


}
