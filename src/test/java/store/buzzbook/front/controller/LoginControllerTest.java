package store.buzzbook.front.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.common.config.SecurityConfig;
import store.buzzbook.front.controller.user.LoginController;
import store.buzzbook.front.service.cart.CartService;
import store.buzzbook.front.service.jwt.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@Import({CartInterceptor.class, CookieUtils.class, SecurityConfig.class})
@WebMvcTest(LoginController.class)
//@TestPropertySource(properties = {
//        "api.gateway.host=localhost",
//        "api.gateway.port=8080"
//})
@ActiveProfiles("test")
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtService jwtService;

    @Test
    public void testLogin_withoutError() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/register/login"))
                .andExpect(model().attributeDoesNotExist("error"));
    }

    @Test
    public void testLogin_withError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/register/login"))
                .andExpect(model().attribute("error", "아이디나 비밀번호를 확인해주세요."));
    }
}
