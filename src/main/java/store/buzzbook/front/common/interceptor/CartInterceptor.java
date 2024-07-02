package store.buzzbook.front.common.interceptor;


import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.cart.CartNotFoundException;
import store.buzzbook.front.common.exception.cart.InvalidCartUuidException;
import store.buzzbook.front.common.exception.user.UserTokenException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.service.cart.CartService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartInterceptor implements HandlerInterceptor {
	private final CartService cartService;
	private final CookieUtils cookieUtils;

	@Override
	public boolean preHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) throws Exception {
		log.debug("preHandled cart path");
		Optional<String> jwt = getJwtFromHeader(request);
		Optional<Cookie> cartCodeCookie = cookieUtils.getCartIdFromRequest(request);

		//회원인 경우
		if (jwt.isPresent()) {
			presentedJwt(jwt.get(),cartCodeCookie, request, response);
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

	private void presentedJwt(String jwt, Optional<Cookie> cartCodeCookie, HttpServletRequest request, HttpServletResponse response){
		Optional<Long> userId = getUserIdFromJwt(jwt);

		//jwt로 받은 회원 아이디가 비어있을 경우.
		if(userId.isEmpty()) {
			//JWT에서 회원 정보를 못찾음 혹은 잘못된 JWT
			throw new UserTokenException();
		}
		//카트 쿠키가 비어 있는 경우
		if (cartCodeCookie.isEmpty()) {
			// uuid를 새로 받아서 준다.
			String uuid = cartService.getUuidByUserId(userId.get());
			request.setAttribute(CookieUtils.COOKIE_CART_KEY, uuid);
			response.addCookie(cookieUtils.wrapCartCookie(uuid));
			log.debug("쿠키에 카트 코드가 저장되었습니다. : {}", uuid);
		}else {
			String uuid = cartCodeCookie.get().getValue();
			request.setAttribute(CookieUtils.COOKIE_CART_KEY, uuid);
		}

	}

	//제공된 jwt로 auth서버에서 userId를 가져옴
	private Optional<Long> getUserIdFromJwt(final String jwt) {

		//todo jwt서버에서 받아와야함
		return Optional.empty();
	}

	private Optional<String> getJwtFromHeader(HttpServletRequest request) {
		String jwt = request.getHeader("jwt");

		if (jwt == null) {
			return Optional.empty();
		}

		return Optional.of(jwt);
	}

	private boolean isValidUuid(String uuid) {
		try {
			UUID.fromString(uuid);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

}

