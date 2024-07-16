package store.buzzbook.front.controller.cart;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.cart.CreateCartDetailRequest;
import store.buzzbook.front.service.cart.CartService;

@RestController
@RequiredArgsConstructor
public class CartRestController {
	private final CartService cartService;

	@PostMapping("/cart/detail")
	public ResponseEntity<Void> addCart(HttpServletRequest request, @Valid @RequestBody CreateCartDetailRequest createCartDetailRequest) {
		String uuid = cartService.getCartIdFromRequest(request);
		cartService.createCartDetail(uuid,createCartDetailRequest);
		return ResponseEntity.ok().build();
	}
}
