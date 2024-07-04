package store.buzzbook.front.controller.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import store.buzzbook.front.client.jwt.JwtClient;
import store.buzzbook.front.common.annotation.JwtValidate;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.util.CookieUtils;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LogoutController {

    private final CookieUtils cookieUtils;
    private final JwtClient jwtClient;

    @JwtValidate
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {

        Optional<Cookie> accessCookie = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY);
        Optional<Cookie> refreshCookie = cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY);

        String accessToken = null;
        String refreshToken = null;

        if (accessCookie.isPresent()) {
            accessToken = ("Bearer " + accessCookie.get().getValue()).trim();
        }

        if (refreshCookie.isPresent()) {
            refreshToken = ("Bearer " + refreshCookie.get().getValue()).trim();
        }

        // 다른 서버의 API 호출
        ResponseEntity<Void> responseEntity = jwtClient.logout(accessToken, refreshToken);

        log.info("response entity: {}", responseEntity.getStatusCode());

        if (responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new AuthorizeFailException("Invalid access token and refresh token");
        }

        cookieUtils.logout(request, response);

        // 리다이렉트 처리
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/home");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
