package store.buzzbook.front.controller.cart;

import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.exception.cart.NullCartException;
import store.buzzbook.front.dto.cart.GetCartResponse;
import store.buzzbook.front.service.cart.CartService;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;


    @GetMapping("/cart")
    public String getCartByCartId(Model model, HttpSession session, @RequestParam(required = false) Long cartId) {
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
        session.setAttribute("cart", cartResponse);


        return "index";
    }

    @GetMapping("/cart/delete")
    public String deleteByDetailId(Model model, HttpSession session, @RequestParam Long detailId) {
        GetCartResponse cartResponse = null;

        if(Objects.isNull(session.getAttribute("cart"))){
            throw new NullCartException();
        }

        Long cartId = ((GetCartResponse) session.getAttribute("cart")).getId();

        cartService.deleteCartDetail(cartId,detailId);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");

        cartResponse = (GetCartResponse)session.getAttribute("cart");

        //todo cart가 null 오류 처리

        model.addAttribute("cart", cartResponse);

        return "index";
    }

    @PostMapping("/cart")
    public String updateCartDetail(Model model, @RequestParam(name = "cartId")Long cartId, @RequestParam Long id, @RequestParam Integer quantity) {
        GetCartResponse cartResponse = null;

        cartResponse = cartService.updateCart(id, quantity, cartId);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", cartResponse);


        return "index";
    }


    @DeleteMapping("/cart")
    public String deleteCart(Model model, HttpSession session, @RequestParam Long cartId) {
        cartService.deleteAll(cartId);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");

        GetCartResponse getCartResponse = (GetCartResponse)session.getAttribute("cart");

        getCartResponse.deleteAllCartDetails();

        session.setAttribute("cart", getCartResponse);
        model.addAttribute("cart", getCartResponse);


        return "index";
    }


}
