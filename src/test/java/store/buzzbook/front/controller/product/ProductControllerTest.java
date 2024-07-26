// package store.buzzbook.front.controller.product;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
// import static org.mockito.BDDMockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// import java.time.LocalDate;
// import java.util.Collections;
// import java.util.LinkedHashMap;
//
// import store.buzzbook.front.client.coupon.CouponPolicyClient;
// import store.buzzbook.front.client.product.CategoryClient;
// import store.buzzbook.front.client.product.ProductClient;
// import store.buzzbook.front.client.product.ProductTagClient;
// import store.buzzbook.front.common.interceptor.CartInterceptor;
// import store.buzzbook.front.dto.product.BookResponse;
// import store.buzzbook.front.dto.product.CategoryResponse;
// import store.buzzbook.front.dto.product.ProductDetailResponse;
// import store.buzzbook.front.dto.product.ProductResponse;
//
// @ActiveProfiles("test")
// @WebMvcTest(ProductController.class)
// class ProductControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@MockBean
// 	private ProductClient productClient;
//
// 	@MockBean
// 	private CouponPolicyClient couponPolicyClient;
//
// 	@MockBean
// 	private ProductTagClient productTagClient;
//
// 	@MockBean
// 	private CategoryClient categoryClient;
//
// 	@InjectMocks
// 	private ProductController productController;
//
// 	@MockBean
// 	private CartInterceptor cartInterceptor;
//
// 	@BeforeEach
// 	void setUp() {
// 		MockitoAnnotations.openMocks(this);
// 		mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
// 	}
//
// 	@Test
// 	@DisplayName("상품 목록 조회 메서드 호출 테스트")
// 	void testGetAllProduct() throws Exception {
// 		Page<ProductResponse> productPage = new PageImpl<>(Collections.emptyList());
//
// 		LinkedHashMap<Integer, String> parentCategory = new LinkedHashMap<>();
// 		parentCategory.put(1, "Parent Category");
//
// 		LinkedHashMap<Integer, String> subCategory = new LinkedHashMap<>();
// 		subCategory.put(1, "Sub Category");
//
// 		CategoryResponse categoryResponse = new CategoryResponse(0, "All", parentCategory, subCategory);
//
// 		given(productClient.getAllProducts(anyString(), anyString(), anyInt(), anyString(), anyInt(), anyInt())).willReturn(productPage);
// 		given(categoryClient.getCategory(anyInt())).willReturn(new ResponseEntity<>(categoryResponse, HttpStatus.OK));
// 		given(productTagClient.getTagsByProductId(anyInt())).willReturn(new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));
//
// 		mockMvc.perform(get("/product")
// 				.param("query", "Test Query")
// 				.param("categoryId", "1")
// 				.param("orderBy", "name")
// 				.param("page", "1")
// 				.param("size", "10"))
// 			.andExpect(status().isOk());
// 	}
//
// 	@Test
// 	@DisplayName("상품 상세 조회 메서드 호출 테스트")
// 	void testGetProductDetail() throws Exception {
// 		LinkedHashMap<Integer, String> parentCategory = new LinkedHashMap<>();
// 		parentCategory.put(1, "Parent Category");
//
// 		LinkedHashMap<Integer, String> subCategory = new LinkedHashMap<>();
// 		subCategory.put(1, "Sub Category");
//
// 		CategoryResponse categoryResponse = new CategoryResponse(1, "Category", parentCategory, subCategory);
//
// 		ProductResponse productResponse = new ProductResponse(
// 			1,
// 			10,
// 			"Test Product",
// 			"This is a test product.",
// 			1000,
// 			LocalDate.now(),
// 			5,
// 			"test/path",
// 			"SALE",
// 			categoryResponse,
// 			Collections.emptyList()
// 		);
//
// 		BookResponse bookResponse = BookResponse.builder()
// 			.id(1L)
// 			.title("Test Book")
// 			.authors(Collections.singletonList("Author"))
// 			.description("Description")
// 			.isbn("1234567890")
// 			.publisher("Publisher")
// 			.publishDate(LocalDate.now())
// 			.product(productResponse)
// 			.build();
//
// 		ProductDetailResponse productDetailResponse = new ProductDetailResponse();
// 		productDetailResponse.setBook(bookResponse);
// 		productDetailResponse.setReviews(Collections.emptyList());
//
// 		given(productClient.getProductDetail(anyInt())).willReturn(productDetailResponse);
// 		given(couponPolicyClient.getSpecificCouponPolicies(anyInt())).willReturn(Collections.emptyList());
// 		given(productClient.getAllProducts(anyString(), anyString(), anyInt(), anyString(), anyInt(), anyInt()))
// 			.willReturn(new PageImpl<>(Collections.emptyList()));
//
// 		mockMvc.perform(get("/product/{id}", 1))
// 			.andExpect(status().isOk());
// 	}
// }