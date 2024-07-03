package store.buzzbook.front.controller.cart;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.cart.CreateCartDetailRequest;
import store.buzzbook.front.service.cart.CartService;

@RestController
@RequiredArgsConstructor
public class CartRestController {
	private final CartService cartService;

	@PostMapping("/cart/detail")
	public void addCart(HttpServletRequest request, @RequestBody CreateCartDetailRequest createCartDetailRequest) {
		String uuid = cartService.getCartIdFromRequest(request);
		cartService.createCartDetail(uuid,createCartDetailRequest);
	}
}
