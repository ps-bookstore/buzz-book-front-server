package store.buzzbook.front.common.interceptor;


import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
		log.info("preHandled cart request");
		Optional<String> jwt = getJwtFromHeader(request);
		Optional<Cookie> cartCodeCookie = cookieUtils.getCartIdFromRequest(request);


		//회원인 경우
		if (jwt.isPresent()) {
			presentedJwt(jwt,cartCodeCookie, response);
			return true;
		} else if (cartCodeCookie.isEmpty()) {
			// 비회원인데 장바구니 코드가 없을 경우 새로 생성
			Long newCartId = cartService.createCartAndSaveCookie(response);
			log.debug("비회원에게 새로운 카트 코드가 발급되었습니다. : {}", newCartId);
			return true;
		} else {
			// 비회원인데 장바구니가 코드가 있을 경우
			// 위조 확인?
		}

		if(cartCodeCookie.isPresent() && cartCodeCookie.get().getMaxAge() < CookieUtils.REFRESH_COOKIE_AGE) {
			// 비회원인데 쿠키 유효기간이 6일 이내 일경우 유효기간을 갱신 해준다.
			cartCodeCookie.get().setMaxAge(CookieUtils.DEFAULT_COOKIE_AGE);
			response.addCookie(cartCodeCookie.get());

			log.debug("비회원의 카트 코드가 갱신 되었습니다.");
		}
		return true;
	}

	private void presentedJwt(Optional<String> jwt, Optional<Cookie> cartCodeCookie, HttpServletResponse response){
		Optional<Long> userId = getUserIdFromJwt(jwt.get());

		//jwt로 받은 회원 아이디가 비어있을 경우.
		if(userId.isEmpty()) {
			//JWT에서 회원 정보를 못찾음 혹은 잘못된 JWT
			throw new UserTokenException();
		}

		Long cartId = getCartIdFromJwt(jwt.get()).orElseThrow(UserTokenException::new);

		//카트 쿠키가 비어 있는 경우 or 카트 쿠키가 있지만 잘못된 경우 or 위조된 경우
		if (cartCodeCookie.isEmpty()
		|| !cartId.equals(Long.parseLong(cartCodeCookie.get().getValue()))) {
			// JWT가 있을 경우 회원 ID를 추출하고 회원 장바구니 처리
			response.addCookie(cookieUtils.wrapCookie(cartId));
			log.debug("쿠키에 카트 코드가 저장되었습니다. : {}", cartId);
		}

	}

	//제공된 jwt로 auth서버에서 cartId를 가져옴
	private Optional<Long> getCartIdFromJwt(final String jwt) {

		//todo jwt서버에서 받아와야함
		return Optional.empty();
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

}

