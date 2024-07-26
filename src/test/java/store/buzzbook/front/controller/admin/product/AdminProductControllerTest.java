package store.buzzbook.front.controller.admin.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.client.product.ProductTagClient;
import store.buzzbook.front.client.product.TagClient;
import store.buzzbook.front.common.config.SecurityConfig;
import store.buzzbook.front.common.config.WebConfig;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.product.CategoryResponse;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;
import store.buzzbook.front.dto.product.ProductUpdateForm;
import store.buzzbook.front.dto.product.ProductUpdateRequest;
import store.buzzbook.front.dto.product.TagResponse;
import store.buzzbook.front.service.jwt.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.buzzbook.front.common.util.CookieUtils.*;

@ActiveProfiles("test")
@WebMvcTest(AdminProductController.class)
@Import({WebConfig.class, SecurityConfig.class})
class AdminProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductClient productClient;

	@MockBean
	private ProductTagClient productTagClient;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private CookieUtils cookieUtils;

	@MockBean
	private CartInterceptor cartInterceptor;

	@MockBean
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@MockBean
	private TagClient tagClient;

	@Mock
	private ProductResponse productResponse;

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

	// @Test
	// void testAdminPage() throws Exception {
	// 	// Arrange
	// 	CategoryResponse categoryResp = new CategoryResponse(1, "Test Category", null, null);
	//
	// 	ProductResponse product = new ProductResponse(1, 100, "Test Product", "Description", 10000, null, 5, "www.naver.com", "SALE", categoryResp, Collections.emptyList());
	// 	Page<ProductResponse> productPage = new PageImpl<>(Collections.singletonList(product));
	//
	// 	// Here, ensure that all expected parameters are accounted for and match the method signature
	// 	when(productClient.getAllProducts(anyString(), anyString(), any(), anyString(), anyInt(), anyInt()))
	// 		.thenReturn(productPage);
	//
	// 	// Act & Assert
	// 	mockMvc.perform(get("/admin/product")
	// 			.param("page", "1")
	// 			.param("size", "5"))
	// 		.andExpect(status().isOk())
	// 		.andExpect(view().name("admin/index"))
	// 		.andExpect(model().attributeExists("products"))
	// 		.andExpect(model().attributeExists("pageable"))
	// 		.andExpect(model().attributeExists("selectedStockStatus"))
	// 		.andExpect(model().attributeExists("query"))
	// 		.andExpect(model().attributeExists("stockStatusOptions"));
	// }

	@Test
	void testAddProductForm() throws Exception {
		mockMvc.perform(get("/admin/product/add"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/pages/product-manage-add"));
	}

	@Test
	void testAddProductSubmit() throws Exception {
		ProductRequest productRequest = ProductRequest.builder()
			.productName("New Product")
			.stock(100)
			.description("Description of the new product")
			.price(10000)
			.forwardDate("2024-01-01")
			.thumbnailPath("/images/new_product.png")
			.stockStatus("SALE")
			.categoryId(1)
			.build();

		mockMvc.perform(post("/admin/product/add")
				.flashAttr("productRequest", productRequest))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/product?query=New Product"));
	}

	@Test
	void testEditProductForm() throws Exception {
		// Create a CategoryResponse object with valid values
		CategoryResponse categoryResponse = new CategoryResponse(1, "Test Category", null, null);

		// Ensure category is not null in ProductResponse
		ProductResponse productResponse = new ProductResponse(1, 100, "Test Product", "Description", 10000, null, 5, null, "SALE", categoryResponse, Collections.emptyList());

		// Mocking the ProductClient call
		when(productClient.getProductById(anyInt())).thenReturn(productResponse);

		// Act & Assert
		mockMvc.perform(get("/admin/product/edit/{id}", 1))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("product"))
			.andExpect(model().attribute("page", "product-manage-edit"));
	}

	@Test
	void testEditProduct() throws Exception {

		ProductUpdateForm productUpdateForm = ProductUpdateForm.builder()
			.id(3)
			.stock(50)
			.price(2000)
			.name("Updated Product")
			.description("Updated Description")
			.thumbnailPath("/images/updated_product.png")
			.stockStatus("SALE")
			.categoryId(1)
			.build();

		ProductUpdateRequest productReq = ProductUpdateForm.convertFormToReq(productUpdateForm);

		when(productClient.updateProduct(productUpdateForm.getId(),productReq))
			.thenReturn(productResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/admin/product/edit/{id}", 2)
				.param("id", String.valueOf(productUpdateForm.getId()))
				.param("stock", String.valueOf(productUpdateForm.getStock()))
				.param("price", String.valueOf(productUpdateForm.getPrice()))
				.param("name", productUpdateForm.getName())
				.param("description", productUpdateForm.getDescription())
				.param("thumbnailPath", productUpdateForm.getThumbnailPath())
				.param("stockStatus", productUpdateForm.getStockStatus())
				.param("categoryId", String.valueOf(productUpdateForm.getCategoryId())))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testSearchProducts() throws Exception {
		ProductResponse productResponse = new ProductResponse(1, 100, "Test Product", "Description", 10000, null, 5, null, "SALE", null, Collections.emptyList());
		List<ProductResponse> productList = Arrays.asList(productResponse);
		when(productClient.searchProducts(anyString())).thenReturn(productList);

		mockMvc.perform(get("/admin/product/search")
				.param("query", "test"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]").exists())
			.andExpect(jsonPath("$[0].id").value(productResponse.getId()))
			.andExpect(jsonPath("$[0].productName").value(productResponse.getProductName()));
	}

	@Test
	void testEditProductTags() throws Exception {
		List<String> productTags = Arrays.asList("tag1", "tag2");
		when(productTagClient.getTagsByProductId(anyInt())).thenReturn(ResponseEntity.ok(productTags));

		TagResponse tagResponse = new TagResponse(1, "Test Tag");
		when(tagClient.getAllTags()).thenReturn(ResponseEntity.ok(Collections.singletonList(tagResponse)));

		mockMvc.perform(get("/admin/product/tags/{productId}", 1))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/pages/product-manage-tags"))
			.andExpect(model().attributeExists("productId"))
			.andExpect(model().attributeExists("productTags"))
			.andExpect(model().attributeExists("allTags"));
	}
}