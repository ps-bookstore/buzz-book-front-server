package store.buzzbook.front.common.error;

import feign.FeignException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.auth.CouponAuthorizeFailException;
import store.buzzbook.front.common.exception.cart.InvalidCartUuidException;
import store.buzzbook.front.common.exception.user.ActivateFailException;
import store.buzzbook.front.common.exception.user.DormantUserException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.util.CookieUtils;

import java.net.BindException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	private static final String INDEX = "index";
	private static final String ERROR = "error";
	private static final String STATE = "state";
	private static final String PAGE = "page";

	private CookieUtils cookieUtils;

	@ExceptionHandler(ActivateFailException.class)
	public ResponseEntity<Void> activateFail(ActivateFailException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(DormantUserException.class)
	public String handleDormantUserException(DormantUserException e) {
		String token = e.getDormantToken();
		return String.format("redirect:/activate?token=%s", token);
	}

	@ExceptionHandler({AuthorizeFailException.class, FeignException.Unauthorized.class})
	public String handleAuthorizeFailException(Exception e, Model model, HttpServletRequest request,
		HttpServletResponse response) {
		model.addAttribute(PAGE, ERROR);
		model.addAttribute(ERROR, "해당 페이지에 접근할 수 없습니다. 관리자 계정으로 로그인하세요.");
		model.addAttribute(STATE, "401");

		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_ACCESS_KEY);
		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_JWT_REFRESH_KEY);
		log.error(e.getMessage());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		return INDEX;
	}

	@ExceptionHandler({FeignException.class})
	public String handleFeignException(FeignException e, Model model, HttpServletRequest request,
		HttpServletResponse response) {
		model.addAttribute(PAGE, ERROR);
		model.addAttribute(ERROR, "해당 페이지에 접근할 수 없습니다. 관리자 계정으로 로그인하세요.");
		model.addAttribute(STATE, e.status());

		log.error(e.getMessage());
		response.setStatus(e.status());
		return INDEX;
	}

	@ExceptionHandler(CouponAuthorizeFailException.class)
	public void handleCouponAuthorizeFailException(Exception e, Model model, HttpServletRequest request,
		HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		model.addAttribute(STATE, "401");
	}

	@ExceptionHandler(InvalidCartUuidException.class)
	public String handleInvalidCartUuidException(Exception e, Model model, HttpServletRequest request,
		HttpServletResponse response) {
		model.addAttribute(PAGE, ERROR);
		model.addAttribute(ERROR, "잘못된 요청입니다. 다시 시도해주세요.");
		model.addAttribute(STATE, "400");

		cookieUtils.deleteCookie(request, response, CookieUtils.COOKIE_CART_KEY);
		log.error(e.getMessage());
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return INDEX;
	}

	@ExceptionHandler({UnknownApiException.class})
	public String handleUnknownApiException(Exception e, Model model, HttpServletResponse response) {
		model.addAttribute(PAGE, ERROR);
		model.addAttribute(ERROR, "문제가 발생했습니다. 잠시 후에 시도해주십시오.");
		model.addAttribute(STATE, "500");
		log.error(e.getMessage());
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return INDEX;
	}

	@ExceptionHandler({NoResourceFoundException.class, BindException.class})
	public String handleNoResourceFoundException(NoResourceFoundException e, Model model,
		HttpServletResponse response) {
		model.addAttribute(PAGE, ERROR);
		model.addAttribute(ERROR, "페이지를 찾을 수 없습니다. 이전 페이지로 돌아가세요.");
		model.addAttribute(STATE, "404");
		log.error(e.getMessage());
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return INDEX;
	}

	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleBadRequestException(BadRequestException e, Model model, HttpServletResponse response) {
		model.addAttribute(PAGE, ERROR);
		model.addAttribute(ERROR, "잘못된 요청입니다. 다시 시도해주세요.");
		model.addAttribute(STATE, "400");
		log.error(e.getMessage());
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return INDEX;
	}

	@Lazy
	@Autowired
	public void setCookieUtils(CookieUtils cookieUtils) {
		this.cookieUtils = cookieUtils;
	}
}
