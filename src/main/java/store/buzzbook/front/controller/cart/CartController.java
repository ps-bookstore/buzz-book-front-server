package store.buzzbook.front.controller.cart;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.exception.cart.CartNotFoundException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.GetCartResponse;
import store.buzzbook.front.service.cart.CartService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final CookieUtils cookieUtils;

    @GetMapping
    public String getCartByCartId(Model model, HttpServletRequest request) {
        Long cartId = getCartId(request);
        List<CartDetailResponse> cartResponse = cartService.getCartByCartId(cartId);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", cartResponse);


        return "index";
    }

    @GetMapping("/delete")
    public String deleteByDetailId(Model model, HttpServletRequest request, @RequestParam("detailId") Long detailId) {
        Long cartId = getCartId(request);
        List<CartDetailResponse> deletedCartDetail = cartService.deleteCartDetail(cartId,detailId);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", deletedCartDetail);

        return "index";
    }


    //현재는 1 상품 씩 수정하지만 나중에 한번에 처리하는걸로 바꾸기
    @PostMapping
    public String updateCartDetail(Model model, HttpServletRequest request, @RequestParam("detailId") Long detailId, @RequestParam Integer quantity) {
        Long cartId = getCartId(request);
        List<CartDetailResponse> cartResponse = cartService.updateCart(cartId, detailId, quantity);

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", cartResponse);

        return "index";
    }


    @DeleteMapping
    public String deleteCart(Model model, HttpServletRequest request) {
        Long cartId = getCartId(request);
        cartService.deleteAll(cartId);

        //todo jwt에서 userId, cartId 꺼내서 cartdto 빌드해서 삽입

        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        model.addAttribute("cart", null);


        return "index";
    }

    private Long getCartId(HttpServletRequest request){
        return Long.parseLong(cookieUtils.getCartIdFromRequest(request)
            .orElseThrow().getValue());
    }


}
