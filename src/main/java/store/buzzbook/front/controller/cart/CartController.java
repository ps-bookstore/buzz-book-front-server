package store.buzzbook.front.controller.cart;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {
    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("page", "cart");
        model.addAttribute("title", "장바구니");
        return "index";
    }
}
