package store.buzzbook.front.controller.question;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuestionController {
    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("title", "자주묻는질문");
        model.addAttribute("page", "faq");
        return "index";
    }
}
