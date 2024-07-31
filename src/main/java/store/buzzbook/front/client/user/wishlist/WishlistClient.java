package store.buzzbook.front.client.user.wishlist;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.common.interceptor.FeignInterceptor;
import store.buzzbook.front.dto.product.ProductResponse;

@FeignClient(name = "WishlistClient", url = "http://${api.gateway.host}:${api.gateway.port}/api/wishlist",
	configuration = {FeignInterceptor.class})
public interface WishlistClient {

	@GetMapping
	ResponseEntity<Page<ProductResponse>> getWishlist(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size);

	@PostMapping("/{productId}")
	ResponseEntity<Long> createWishlist(@PathVariable int productId);

	@DeleteMapping("/{id}")
	ResponseEntity<Void> deleteWishlist(@PathVariable long id);

	@GetMapping("/{productId}")
	ResponseEntity<Boolean> checkWishlist(@PathVariable int productId);

}
