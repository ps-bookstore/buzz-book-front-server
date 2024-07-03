package store.buzzbook.front.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.dto.user.CustomUserDetails;
import store.buzzbook.front.dto.user.JwtLoginUser;
import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.service.user.UserService;

@Service
@Slf4j
public class LoginUserDetailsService implements UserDetailsService {
    private UserService userService;

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

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
