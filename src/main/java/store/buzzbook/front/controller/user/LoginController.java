package store.buzzbook.front.controller.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public Object login(@RequestParam(value = "error", required = false) String error,
                        Model model, HttpServletRequest request) {
        // 이미 로그인을 한 상태(Authorization)이 있다면 로그인페이지 못가게 하기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return new RedirectView("/home");
                }
            }
        }

        // 쿠키가 없으면 로그인 페이지를 반환합니다.
        if (error != null) {
            model.addAttribute("error", "아이디나 비밀번호를 확인해주세요.");
        }
        return "pages/register/login";
    }

    @GetMapping("/auth/login/wait")
    public String loginWait() {
        return "pages/register/login-wait";
    }

}
