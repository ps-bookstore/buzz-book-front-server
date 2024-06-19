package store.buzzbook.front.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {
    @GetMapping("/product")
    public String product(Model model) {
        model.addAttribute("page", "product");
        model.addAttribute("title", "상품");
        return "index";
    }

    @GetMapping("/product-detail")
    public String productDetail(Model model) {
        model.addAttribute("page", "product-detail");
        model.addAttribute("title", "상품상세");
        return "index";
    }
}
