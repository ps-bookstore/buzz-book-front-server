package store.buzzbook.front.controller.order;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {
    @GetMapping("/order")
    public String order(Model model) {
        model.addAttribute("page", "order");
        model.addAttribute("title", "주문하기");
        // todo 객체 넣기
        model.addAttribute("myInfo", null);
        model.addAttribute("addressInfo", null);
        model.addAttribute("createOrderRequest", null);
        model.addAttribute("createOrderDetailRequestList", null);
        model.addAttribute("packages", null);
        model.addAttribute("itemsInCart", null);

        return "index";
    }

    @GetMapping("/my-page")
    public String myPage(Model model) {

        model.addAttribute("page", "my-page");
        model.addAttribute("myOrders", null);
        return "index";
    }
}
