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

		RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


		UserInfo userInfo = null;
		try{
			userInfo = userService.successLogin((String)request.getAttribute("id"));
			request.getSession().setAttribute("user", userInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			redirectStrategy.sendRedirect(request, response, "/login");
		}

		log.info("login success");
		// todo redis 추가
		// if(Objects.nonNull(redisTemplate.opsForValue().get(sessionId))){
		// 	redisTemplate.delete(sessionId);
		// }

		redirectStrategy.sendRedirect(request, response, "/home");
	}
}
