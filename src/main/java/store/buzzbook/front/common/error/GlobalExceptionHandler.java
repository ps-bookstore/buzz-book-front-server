package store.buzzbook.front.common.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.cart.InvalidCartUuidException;
import store.buzzbook.front.common.util.CookieUtils;

@ControllerAdvice
public class GlobalExceptionHandler {

    private CookieUtils cookieUtils;

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("page", "error");
        model.addAttribute("error", "에러메세지: " + e.getMessage());
        return "index";
    }

    @ExceptionHandler(AuthorizeFailException.class)
    public String handleAuthorizeFailException(Exception e, Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("page", "error");
        model.addAttribute("error", "에러메세지" + e.getMessage());

        cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_ACCESS_KEY);
        cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_REFRESH_KEY);

        return "index";
    }


    @ExceptionHandler(InvalidCartUuidException.class)
    public String handleInvalidCartUuidException(Exception e, Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("page", "error");
        model.addAttribute("error", "에러메세지" + e.getMessage());

        cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_CART_KEY);

        return "index";
    }

    @Lazy
    @Autowired
    public void setCookieUtils(CookieUtils cookieUtils) {
        this.cookieUtils = cookieUtils;
    }
}
