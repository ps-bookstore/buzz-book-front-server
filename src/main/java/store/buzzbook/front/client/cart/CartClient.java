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

import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.CreateCartDetailRequest;

@FeignClient(name = "cartClient",url = "http://${api.core.host}:" + "${api.core.port}/api/cart")
public interface CartClient {
	@GetMapping("/{uuid}")
	ResponseEntity<List<CartDetailResponse>> getCartByUuid(@PathVariable("uuid") String uuid);

	@DeleteMapping("/{uuid}")
	ResponseEntity<Void> deleteAllCartDetail(@PathVariable("uuid") String uuid);

	@GetMapping("/guest")
	ResponseEntity<String> createCart();

	@PostMapping("/{uuid}/detail")
	ResponseEntity<Void> createCartDetail(@PathVariable String uuid, @RequestBody CreateCartDetailRequest createCartDetailRequest);

	@DeleteMapping("/{uuid}/detail/{detailId}")
	ResponseEntity<List<CartDetailResponse>> deleteCartDetail(@PathVariable("uuid") String uuid, @PathVariable("detailId") Long detailId);

	@PutMapping("/{uuid}/detail/{detailId}")
	ResponseEntity<Void> updateCartDetail(@PathVariable String uuid,@PathVariable Long detailId, @RequestParam Integer quantity);

	@GetMapping
	ResponseEntity<String> getUuidByUserId(@RequestParam("userId") Long userId);



}
