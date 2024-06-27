package store.buzzbook.front.common.handler;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.dto.user.UserInfo;
import store.buzzbook.front.service.user.UserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	private final UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		UserInfo userInfo = null;
		try {
			userInfo = userService.successLogin(authentication.getName());
			request.getSession().setAttribute("user", userInfo);
			log.info("login success");
			response.sendRedirect(request.getContextPath() + "/home");

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response.sendRedirect(request.getContextPath() + "/login");

		}
	}
}
