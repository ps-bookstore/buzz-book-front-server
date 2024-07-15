package store.buzzbook.front.controller.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.product.CategoryClient;
import store.buzzbook.front.dto.product.CategoryRequest;
import store.buzzbook.front.dto.product.CategoryResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/category")
public class AdminCategoryController {

	private final CategoryClient client;

	//TODO @ProductJwtValidate
	@GetMapping
	public String adminCategoryPage(
		@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
		@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
		Model model) {

		ResponseEntity<Page<CategoryResponse>> response = client.getCategory(pageNo, pageSize);
		Page<CategoryResponse> categories = response.getBody();
		// List<CategoryResponse> categoriesList = categories.getContent();

		model.addAttribute("categoryPages", categories);
		// model.addAttribute("categories", categoriesList);

		return "/admin/pages/categories";
	}

	@PostMapping
	public ResponseEntity<String> saveCategory(//@Validated
		@RequestBody CategoryRequest category) {


		Integer id = category.getParentCategoryId();
		log.info("{}", id);
		String name = category.getName();
		log.info("{}", name);

		ResponseEntity<CategoryResponse> response = client.createCategory(category);

		if (response.getStatusCode() == HttpStatus.CREATED)
			return ResponseEntity.ok().build();
		return ResponseEntity.badRequest().body("등록 실패");
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateCategory(@RequestBody CategoryRequest category, @PathVariable int id) {
		ResponseEntity<CategoryResponse> response = client.updateCategory(id, category);
		if (response.getStatusCode() == HttpStatus.OK)
			return ResponseEntity.ok().build();
		return ResponseEntity.badRequest().body("수정 실패");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCategory(@PathVariable("id") int id) {
		ResponseEntity<String> response = client.deleteCategory(id);
		if (response.getStatusCode() == HttpStatus.OK)
			return response;
		return ResponseEntity.badRequest().body("삭제 실패");
	}
}
