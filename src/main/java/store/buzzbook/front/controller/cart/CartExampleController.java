package store.buzzbook.front.controller.cart;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.GetCartResponse;
import store.buzzbook.front.service.cart.CartService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart/example")
public class CartExampleController {
	private final CartService cartService;

	//참고용 페이지. 추후 삭제
	@GetMapping
	public String getCartExample(Model model, HttpSession session, @RequestParam(required = false) Long cartId) {
		GetCartResponse cartResponse = null;

		if(Objects.isNull(cartId)) {
			return "redirect:/error";
		}

		if(cartId == 1L){
			List<CartDetailResponse> cartDetailResponseList = new LinkedList<>();

			cartDetailResponseList.add(new CartDetailResponse(1L, 1,
				"test Product", 1, 50000,
				"https://image.yes24.com/goods/127173465/M"));


			cartDetailResponseList.add(new CartDetailResponse(1L, 2,
				"해적", 2, 20000,
				"https://image.yes24.com/goods/12773465/M"));


			cartResponse = new GetCartResponse(
				1L,
				null,
				cartDetailResponseList
			);
		}else {
			cartResponse = new GetCartResponse(
				1L,
				null,
				List.of()
			);
		}

		model.addAttribute("page", "cart");
		model.addAttribute("title", "장바구니");
		model.addAttribute("cart", cartResponse);

		session.setAttribute("cart", cartResponse);

		return "index";
	}


	@GetMapping("/order")
	public String getOrderExample(Model model, HttpSession session) {
		GetCartResponse cartResponse = null;

		cartResponse = (GetCartResponse)session.getAttribute("cart");

		model.addAttribute("page", "cart");
		model.addAttribute("title", "장바구니");
		model.addAttribute("cart", cartResponse);

		return "pages/cart";
	}
}
