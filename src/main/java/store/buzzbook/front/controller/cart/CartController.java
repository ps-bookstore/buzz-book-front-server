package store.buzzbook.front.controller.cart;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.GetCartResponse;
import store.buzzbook.front.service.cart.CartService;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;


    @GetMapping("/cart")
    public String getCartByCartId(Model model, @RequestParam(required = false) Long cartId) {
        GetCartResponse cartResponse = null;

        try {
            cartResponse = cartService.getCartByCartId(cartId);
        }catch (IllegalStateException e) {
            model.addAttribute("message", e.getMessage());
            return "redirect:/error";
        }

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", cartResponse);

        //세션에 cart 정보 저장?


        return "pages/cart";
    }

    @GetMapping("/cart/delete")
    public String deleteByCartId(Model model, @RequestParam Long detailId) {
        GetCartResponse cartResponse = null;

        cartService.deleteCartDetail(detailId);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", cartResponse);

        return "pages/cart";
    }

    @PostMapping("/cart")
    public String updateCartDetail(Model model, @RequestParam Long detailId, @RequestParam Integer quantity) {
        GetCartResponse cartResponse = null;

        cartService.updateCart(detailId, quantity);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", cartResponse);

        //업데이트 타이밍은?

        return "pages/cart";
    }


}
