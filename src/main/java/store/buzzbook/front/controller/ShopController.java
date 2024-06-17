package store.buzzbook.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShopController {
    @GetMapping("shop")
    public String shop(Model model) {
        return "pages/shop";
    }

    @GetMapping("shop-detail")
    public String shopDetail(Model model) {
        return "pages/shop-detail";
    }
}
