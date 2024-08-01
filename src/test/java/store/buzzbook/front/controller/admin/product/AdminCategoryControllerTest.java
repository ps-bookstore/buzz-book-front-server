package store.buzzbook.front.controller.admin.product;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.front.client.product.CategoryClient;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.dto.product.CategoryRequest;
import store.buzzbook.front.dto.product.CategoryResponse;
import store.buzzbook.front.service.jwt.JwtService;

@ActiveProfiles("test")
@WebMvcTest(value = AdminCategoryController.class)
class AdminCategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private CategoryClient categoryClient;
	@MockBean
	private CartInterceptor cartInterceptor;
	@MockBean
	private JwtService jwtService;

	@Test
	@DisplayName("GET adminCategoryPage")
	@WithMockUser(roles = "ADMIN")
	void adminCategoryPageTest() throws Exception {
		int pageNo = 1;
		int pageSize = 10;

		Page<CategoryResponse> pages = new PageImpl<>(new ArrayList<>());

		when(categoryClient.getCategory(pageNo, pageSize)).thenReturn(ResponseEntity.ok(pages));

		mockMvc.perform(get("/admin/category")
				.param("pageNo", String.valueOf(pageNo))
				.param("pageSize", String.valueOf(pageSize)))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"))
			.andExpect(model().attributeExists("categoryPages"))
			.andExpect(model().attribute("categoryPages", pages))
			.andExpect(model().attribute("page", "admin-product-category"));
	}

	@Test
	@DisplayName("POST save Category")
	@WithMockUser(roles = "ADMIN")
	void saveCategoryTest() throws Exception {

		CategoryRequest validCategoryReq = new CategoryRequest();
		validCategoryReq.setName("category name");
		validCategoryReq.setParentCategoryId(5);
		validCategoryReq.setSubCategoryIds(List.of());

		CategoryRequest invalidCategoryReq = new CategoryRequest();
		invalidCategoryReq.setName("category name");
		invalidCategoryReq.setParentCategoryId(-1);
		invalidCategoryReq.setSubCategoryIds(List.of());

		when(categoryClient.createCategory(any(CategoryRequest.class))).thenReturn(
			ResponseEntity.status(HttpStatus.CREATED).body(null));

		mockMvc.perform(post("/admin/category")
				.contentType("application/json")
				.with(csrf())
				.content(objectMapper.writeValueAsString(validCategoryReq)))

			.andExpect(status().isOk());

		mockMvc.perform(post("/admin/category")
				.contentType("application/json")
				.with(csrf())
				.content(objectMapper.writeValueAsString(invalidCategoryReq)))

			.andExpect(status().isBadRequest());

	}

	@Test
	@DisplayName("PUT update Category")
	@WithMockUser(roles = "ADMIN")
	void updateCategoryTest() throws Exception {

		CategoryRequest validCategoryReq = new CategoryRequest();
		validCategoryReq.setName("category name");
		validCategoryReq.setParentCategoryId(5);
		validCategoryReq.setSubCategoryIds(List.of());

		CategoryRequest invalidCategoryReq = new CategoryRequest();
		invalidCategoryReq.setName("category name");
		invalidCategoryReq.setParentCategoryId(-1);
		invalidCategoryReq.setSubCategoryIds(List.of());

		when(categoryClient.updateCategory(anyInt(), any(CategoryRequest.class))).thenReturn(ResponseEntity.ok(null));

		mockMvc.perform(put("/admin/category/{id}", 5)
				.contentType("application/json")
				.with(csrf())
				.content(objectMapper.writeValueAsString(validCategoryReq)))

			.andExpect(status().isOk());

		mockMvc.perform(put("/admin/category/{id}", 5)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
				.content(objectMapper.writeValueAsString(invalidCategoryReq)))

			.andExpect(status().isBadRequest());

	}

	@Test
	@DisplayName("DELETE delete category")
	@WithMockUser(roles = "ADMIN")
	void deleteCategoryTest() throws Exception {

		when(categoryClient.deleteCategory(5)).thenReturn(ResponseEntity.ok(null));

		mockMvc.perform(delete("/admin/category/{id}", 5)
				.with(csrf()))

			.andExpect(status().isOk());


		when(categoryClient.deleteCategory(-1)).thenReturn(ResponseEntity.notFound().build());

		mockMvc.perform(delete("/admin/category/{id}", -1)
				.with(csrf()))

			.andExpect(status().isBadRequest());

	}

}
