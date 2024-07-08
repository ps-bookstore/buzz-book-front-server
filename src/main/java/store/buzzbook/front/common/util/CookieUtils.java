package store.buzzbook.front.common.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;


@Component
public class CookieUtils {
	public static final String COOKIE_CART_KEY = "CART_ID";
	public static final String COOKIE_JWT_ACCESS_KEY = "Authorization";
	public static final String COOKIE_JWT_REFRESH_KEY = "Refresh-Token";
	public static final int DEFAULT_COOKIE_AGE = 60 * 60 * 24 * 7; // 7일
	public static final int REFRESH_COOKIE_AGE = 60 * 60 * 24 * 6; // 6일

	public Optional<Cookie> getCookie(@NotNull HttpServletRequest request, @NotNull String name) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (name.equals(cookie.getName())) {
					return Optional.of(cookie);
				}
			}
		}
		return Optional.empty();
	}

	public Optional<Cookie> getCartIdFromRequest(@NotNull HttpServletRequest request) {
		return getCookie(request, COOKIE_CART_KEY);
	}

	public void logout(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
		deleteCookie(request,response, COOKIE_JWT_ACCESS_KEY);
		deleteCookie(request,response, COOKIE_JWT_REFRESH_KEY);
		deleteCookie(request,response, COOKIE_CART_KEY);
	}

	public void deleteCookie(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
		@NotNull String name) {
		Optional<Cookie> cookie = getCookie(request, name);

		if (cookie.isPresent()) {
			cookie.get().setMaxAge(0);
			response.addCookie(cookie.get());
		}
	}

	public Cookie wrapCartCookie(@NotNull String uuid) {
		return wrapCookie(COOKIE_CART_KEY, uuid);
	}

	public Cookie wrapJwtTokenCookie(@NotNull String token) {
		return wrapCookie(COOKIE_JWT_ACCESS_KEY, token);
	}

	public Cookie wrapRefreshTokenCookie(@NotNull String token) {
		return wrapCookie(COOKIE_JWT_REFRESH_KEY, token);
	}

	public Cookie wrapCookie(String key, String value) {
		Cookie newCartCookie = new Cookie(key, value);
		newCartCookie.setHttpOnly(true);
		newCartCookie.setPath("/");
		newCartCookie.setMaxAge(DEFAULT_COOKIE_AGE);

		return newCartCookie;
	}
}