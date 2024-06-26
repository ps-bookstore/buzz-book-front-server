package store.buzzbook.front.client.cart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.dto.cart.CreateCartDetailRequest;
import store.buzzbook.front.dto.cart.GetCartResponse;

@FeignClient(name = "cartClient", url = "http://localhost:8080/api/cart")
public interface CartClient {
	@GetMapping
	ResponseEntity<GetCartResponse> getCartByCartId(@RequestParam("cartId") String cartId);

	@PostMapping
	ResponseEntity<Void> createCartDetail(@RequestBody CreateCartDetailRequest createCartDetailRequest);

	@DeleteMapping("/{cartDetailId}")
	ResponseEntity<Void> deleteCartDetail(@PathVariable("cartDetailId") Long cartDetailId);

	@DeleteMapping
	ResponseEntity<Void> deleteAllCartDetail(@RequestParam("cartId") Long cartId);


}
