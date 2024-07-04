package store.buzzbook.front.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.auth.CouponAuthorizeFailException;
import store.buzzbook.front.common.exception.cart.InvalidCartUuidException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.util.CookieUtils;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	private CookieUtils cookieUtils;

	@ExceptionHandler(AuthorizeFailException.class)
	public String handleAuthorizeFailException(Exception e, Model model, HttpServletRequest request,
		HttpServletResponse response) {
		model.addAttribute("page", "error");
		model.addAttribute("error", "에러메세지: " + e.getMessage());

		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_REFRESH_KEY);
		log.error(e.getMessage());
		return "index";
	}

	@ExceptionHandler(CouponAuthorizeFailException.class)
	public void handleCouponAuthorizeFailException(Exception e, Model model, HttpServletRequest request,
		HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@ExceptionHandler(InvalidCartUuidException.class)
	public String handleInvalidCartUuidException(Exception e, Model model, HttpServletRequest request,
		HttpServletResponse response) {
		model.addAttribute("page", "error");
		model.addAttribute("error", "에러메세지: " + e.getMessage());

		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_CART_KEY);
		log.error(e.getMessage());
		return "index";
	}

	@ExceptionHandler({UnknownApiException.class})
	public String handleUnknownApiException(Exception e, Model model) {
		model.addAttribute("page", "error");
		model.addAttribute("error", "에러메세지: 문제가 발생했습니다. 잠시 후에 시도해주십시오.");
		log.error(e.getMessage());
		return "index";
	}

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        String formattedErrorMessage = errorMessage(e);
        model.addAttribute("page", "error");
        model.addAttribute("error", formattedErrorMessage);
        log.error(formattedErrorMessage);
        return "index";
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException e, Model model) {
        String formattedErrorMessage = errorMessage(e);
        model.addAttribute("page", "error");
        model.addAttribute("error", "The requested resource was not found.");
        log.error(formattedErrorMessage);
        return "index";
    }

    private String errorMessage(Exception e) {
        return String.format("\n프론트 서버 에러 \n▶️ %s \n▶️ %s", e.getMessage(), e);
    }
    @Lazy
    @Autowired
    public void setCookieUtils(CookieUtils cookieUtils) {
        this.cookieUtils = cookieUtils;
    }

}
