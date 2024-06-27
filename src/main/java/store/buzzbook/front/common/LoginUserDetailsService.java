package store.buzzbook.front.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.user.CustomUserDetails;
import store.buzzbook.front.dto.user.JwtLoginUser;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.service.user.UserService;

@Service
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(LoginUserDetailsService.class);
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        log.info("서비스 로그인 시도 : {}", loginId);

        LoginUserResponse loginUserResponse = userService.requestLogin(loginId);
        String role = loginUserResponse.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";

        JwtLoginUser jwtLoginUser = JwtLoginUser.builder()
            .username(loginUserResponse.loginId())
            .password(loginUserResponse.password())
            .role(role).build();

        return new CustomUserDetails(jwtLoginUser);
    }
}
