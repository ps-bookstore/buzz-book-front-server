package store.buzzbook.front.controller.admin.product;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.buzzbook.front.common.util.CookieUtils.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import store.buzzbook.front.client.product.ProductTagClient;
import store.buzzbook.front.client.product.TagClient;
import store.buzzbook.front.common.config.SecurityConfig;
import store.buzzbook.front.common.config.WebConfig;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.product.TagResponse;
import store.buzzbook.front.service.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@WebMvcTest(AdminProductTagController.class)
@Import({WebConfig.class, SecurityConfig.class})

class AdminProductTagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductTagClient productTagClient;

	@MockBean
	private TagClient tagClient;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private CookieUtils cookieUtils;

	@MockBean
	private CartInterceptor cartInterceptor;

	@MockBean
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		Cookie mockCookie = new Cookie(COOKIE_JWT_ACCESS_KEY, "valid-token");
		when(cookieUtils.getCookie(any(HttpServletRequest.class), anyString())).thenReturn(Optional.of(mockCookie));
		when(jwtService.getInfoMapFromJwt(any(HttpServletRequest.class), any(HttpServletResponse.class)))
			.thenReturn(Map.of(
				JwtService.USER_ID, 1,
				JwtService.LOGIN_ID, "test",
				JwtService.ROLE, "ROLE_ADMIN"
			));
	}

	@Test
	void testGetTagsByProductId() throws Exception {
		List<String> tags = Arrays.asList("tag1", "tag2");
		when(productTagClient.getTagsByProductId(anyInt())).thenReturn(ResponseEntity.ok(tags));

		mockMvc.perform(get("/api/productTags/{productId}", 1))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0]").value("tag1"))
			.andExpect(jsonPath("$[1]").value("tag2"));
	}

	@Test
	void testAddTagToProduct() throws Exception {
		List<Integer> tagIds = Arrays.asList(1, 2);
		mockMvc.perform(post("/api/productTags/{productId}/tags", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(tagIds)))
			.andExpect(status().isOk());
	}

	@Test
	void testRemoveTagFromProduct() throws Exception {
		List<Integer> tagIds = Arrays.asList(1, 2);
		mockMvc.perform(delete("/api/productTags/{productId}/tags", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(tagIds)))
			.andExpect(status().isNoContent());
	}

	@Test
	void testSaveTags() throws Exception {
		List<String> existingTags = Arrays.asList("tag1", "tag2");
		List<TagResponse> allTags = Arrays.asList(new TagResponse(1, "tag1"), new TagResponse(2, "tag2"));
		when(productTagClient.getTagsByProductId(anyInt())).thenReturn(ResponseEntity.ok(existingTags));
		when(tagClient.getAllTags()).thenReturn(ResponseEntity.ok(allTags));

		mockMvc.perform(post("/api/productTags/save")
				.param("productId", "1")
				.param("tagIds", "1", "2"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/product"));
	}
}
