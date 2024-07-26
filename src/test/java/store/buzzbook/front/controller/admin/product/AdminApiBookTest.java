package store.buzzbook.front.controller.admin.product;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.buzzbook.front.common.util.CookieUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.client.product.BookApiClient;
import store.buzzbook.front.common.config.SecurityConfig;
import store.buzzbook.front.common.config.WebConfig;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.product.BookApiRequest;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.product.BookService;

@ActiveProfiles("test")
@WebMvcTest(AdminApiBook.class)
@Import({WebConfig.class, SecurityConfig.class})
class AdminApiBookTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private CookieUtils cookieUtils;

	@MockBean
	private CartInterceptor cartInterceptor;

	@MockBean
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@MockBean
	private BookApiClient bookApiClient;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();

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
	void testShowApiBookPage() throws Exception {
		mockMvc.perform(get("/api/books"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("page"))
			.andExpect(model().attribute("page", "admin-api-book"));
	}


	@Test
	void testSearchBooks() throws Exception {
		// Arrange
		List<BookApiRequest.Item> books = new ArrayList<>();
		books.add(new BookApiRequest.Item());

		// Mocking the BookService to return a list of books
		when(bookService.searchBooks(anyString())).thenReturn(books);

		// Act & Assert
		mockMvc.perform(get("/api/books/search")
				.param("query", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("books"))
			.andExpect(model().attributeExists("query"))
			.andExpect(model().attribute("query", "test"));
	}

	@Test
	void testSearchAndSaveBooks() throws Exception {
		List<BookApiRequest.Item> books = new ArrayList<>();
		books.add(new BookApiRequest.Item(/* Initialize fields appropriately */));

		when(bookService.searchBooks(anyString())).thenReturn(books);

		mockMvc.perform(post("/api/books/search")
				.param("query", "test"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/product"));
	}
}
