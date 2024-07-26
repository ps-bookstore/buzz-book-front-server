package store.buzzbook.front.controller.admin.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.client.product.TagClient;
import store.buzzbook.front.common.config.SecurityConfig;
import store.buzzbook.front.common.config.WebConfig;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.product.TagResponse;
import store.buzzbook.front.service.jwt.JwtService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.buzzbook.front.common.util.CookieUtils.*;

@ActiveProfiles("test")
@WebMvcTest(AdminTagController.class)
@Import({WebConfig.class, SecurityConfig.class})
class AdminTagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TagClient tagClient;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private CookieUtils cookieUtils;

	@MockBean
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@MockBean
	private CartInterceptor cartInterceptor;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();

		Cookie mockCookie = new Cookie(COOKIE_JWT_ACCESS_KEY, "valid-token");
		when(cookieUtils.getCookie(any(HttpServletRequest.class), anyString())).thenReturn(
			Optional.of(mockCookie));
		when(jwtService.getInfoMapFromJwt(any(HttpServletRequest.class), any(HttpServletResponse.class)))
			.thenReturn(Map.of(
				JwtService.USER_ID, 1,
				JwtService.LOGIN_ID, "test",
				JwtService.ROLE, "ROLE_ADMIN"
			));
	}

	@Test
	void testAdminTagsPage() throws Exception {
		TagResponse tagResponse = new TagResponse(1,"테스트 코드");
		Page<TagResponse> tags = new PageImpl<>(Collections.singletonList(tagResponse));
		when(tagClient.getAllTags(any(Integer.class), any(Integer.class), any(String.class)))
			.thenReturn(ResponseEntity.ok(tags));

		mockMvc.perform(get("/admin/tag")
				.param("pageNo", "0")
				.param("pageSize", "10")
				.param("tagName", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("tags"))
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("pageSize"));
	}

	@Test
	void testGetAllTags() throws Exception {
		TagResponse tagResponse = new TagResponse(1,"테스트 코드");
		List<TagResponse> tagList = Arrays.asList(tagResponse);
		when(tagClient.getAllTags()).thenReturn(ResponseEntity.ok(tagList));

		mockMvc.perform(get("/admin/tag/all"))
			.andExpect(status().isOk())
			.andExpect(content().json("[{}]")); // Assuming TagResponse has an empty object structure
	}

	@Test
	void testSaveTag() throws Exception {
		mockMvc.perform(post("/admin/tag")
				.param("tagName", "newTag"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/tag"));
	}

	@Test
	void testDeleteTag() throws Exception {
		mockMvc.perform(post("/admin/tag/{id}", 1))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/tag"));
	}
}