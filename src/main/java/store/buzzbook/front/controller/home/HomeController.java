package store.buzzbook.front.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
public class HomeController {

    @GetMapping("home")
    public String home(Model model) {
        model.addAttribute("page", "main");
        model.addAttribute("title", "메인페이지");
        return "index";
    }
}
