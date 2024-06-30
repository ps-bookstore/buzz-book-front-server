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
	@GetMapping("/{cartId}")
	ResponseEntity<List<CartDetailResponse>> getCartByCartId(@PathVariable("cartId") Long cartId);

	@DeleteMapping("/{cartId}")
	ResponseEntity<Void> deleteAllCartDetail(@PathVariable("cartId") Long cartId);

	@GetMapping
	ResponseEntity<Long> getCartIdByUserId(@RequestParam("userId") Long userId);

	@GetMapping("/guest")
	ResponseEntity<Long> createCart();

	@PostMapping("/{cartId}/detail")
	ResponseEntity<Void> createCartDetail(@PathVariable Long cartId, @RequestBody CreateCartDetailRequest createCartDetailRequest);

	@DeleteMapping("/{cartId}/detail/{detailId}")
	ResponseEntity<List<CartDetailResponse>> deleteCartDetail(@PathVariable("cartId") Long cartId, @PathVariable("detailId") Long detailId);

	@PutMapping("/{cartId}/detail/{detailId}")
	ResponseEntity<Void> updateCartDetail(@PathVariable Long cartId,@PathVariable Long detailId, @RequestParam Integer quantity);





}
