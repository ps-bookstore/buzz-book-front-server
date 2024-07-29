package store.buzzbook.front.common.interceptor;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.cart.InvalidCartUuidException;
import store.buzzbook.front.common.exception.user.UserTokenException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.service.cart.CartService;
import store.buzzbook.front.service.jwt.JwtService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartInterceptor implements HandlerInterceptor {
	private final CartService cartService;
	private final JwtService jwtService;
	private final CookieUtils cookieUtils;
	private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{32}$");

	@Override
	public boolean preHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) throws Exception {
		log.debug("preHandled cart path");
		Optional<Cookie> jwt = cookieUtils.getCookie(request,CookieUtils.COOKIE_JWT_ACCESS_KEY);
		Optional<Cookie> cartCodeCookie = cookieUtils.getCartIdFromRequest(request);

		//회원인 경우
		if (jwt.isPresent()) {
			presentedJwt(cartCodeCookie, request, response);
			return true;
		} else if (cartCodeCookie.isEmpty()) {
			// 비회원인데 장바구니 코드가 없을 경우 새로 생성
			String newCartId = cartService.createCartAndSaveCookie(response);
			request.setAttribute(CookieUtils.COOKIE_CART_KEY, newCartId);
			log.debug("비회원에게 새로운 카트 코드가 발급되었습니다. : {}", newCartId);
			return true;
		} else {
			//비회원인데 장바구니 코드가 있을 경우 1차 유효성 검사
			String uuid = cartCodeCookie.get().getValue();
			if(!isValidUuid(uuid)){
				throw new InvalidCartUuidException();
			}
			request.setAttribute(CookieUtils.COOKIE_CART_KEY, uuid);
		}

		if(cartCodeCookie.get().getMaxAge() < CookieUtils.REFRESH_COOKIE_AGE) {
			// 비회원인데 쿠키 유효기간이 6일 이내 일경우 유효기간을 갱신 해준다.
			cartCodeCookie.get().setMaxAge(CookieUtils.DEFAULT_COOKIE_AGE);
			response.addCookie(cartCodeCookie.get());

			log.debug("비회원의 카트 코드가 갱신 되었습니다.");
		}
		return true;
	}

	private void presentedJwt(Optional<Cookie> cartCodeCookie, HttpServletRequest request, HttpServletResponse response){
		Long userId = jwtService.getUserIdFromJwt(request,response);

		//jwt로 받은 회원 아이디가 비어있을 경우.
		if(Objects.isNull(userId)) {
			//JWT에서 회원 정보를 못찾음 혹은 잘못된 JWT
			throw new UserTokenException();
		}
		//카트 쿠키가 비어 있는 경우
		if (cartCodeCookie.isEmpty()) {
			// uuid를 새로 받아서 준다.
			String uuid = cartService.getUuidByUserId(userId);
			request.setAttribute(CookieUtils.COOKIE_CART_KEY, uuid);
			response.addCookie(cookieUtils.wrapCartCookie(uuid));
			log.debug("쿠키에 카트 코드가 저장되었습니다. : {}", uuid);
		}else {
			String uuid = cartCodeCookie.get().getValue();
			request.setAttribute(CookieUtils.COOKIE_CART_KEY, uuid);
		}

	}

	private boolean isValidUuid(String uuid) {
		return UUID_PATTERN.matcher(uuid).matches();
	}

}

