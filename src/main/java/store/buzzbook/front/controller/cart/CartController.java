package store.buzzbook.front.controller.cart;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.CreateCartDetailRequest;
import store.buzzbook.front.service.cart.CartService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public String getCartByCartId(Model model, HttpServletRequest request) {
        String uuid = cartService.getCartIdFromRequest(request);
        List<CartDetailResponse> cartResponse = cartService.getCartByUuid(uuid);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", cartResponse);


        return "index";
    }

    @GetMapping("/delete")
    public String deleteByDetailId(Model model, HttpServletRequest request, @RequestParam("detailId") Long detailId) {
        String uuid = cartService.getCartIdFromRequest(request);
        List<CartDetailResponse> deletedCartDetail = cartService.deleteCartDetail(uuid,detailId);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", deletedCartDetail);

        return "index";
    }


    @PostMapping
    public String updateCartDetail(HttpServletRequest request, @RequestParam("detailId") Long detailId, @RequestParam Integer quantity) {
        String uuid = cartService.getCartIdFromRequest(request);
        cartService.updateCart(uuid, detailId, quantity);

        return "redirect:/cart";
    }


    @DeleteMapping
    public String deleteCart(Model model, HttpServletRequest request) {
        String uuid = cartService.getCartIdFromRequest(request);
        cartService.deleteAll(uuid);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", List.of());

        return "index";
    }
    @PostMapping
    public String addCart(HttpServletRequest request, @RequestBody CreateCartDetailRequest createCartDetailRequest) {
        String uuid = cartService.getCartIdFromRequest(request);
        cartService.createCartDetail(uuid,createCartDetailRequest);

        return "redirect:/cart";
    }

}
