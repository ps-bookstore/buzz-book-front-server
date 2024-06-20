package store.buzzbook.front.controller.admin.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {
	@GetMapping("/home")
	public String adminHome(Model model) {
		model.addAttribute("page", "main");
		return "admin/index";
	}

}
