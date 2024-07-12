package store.buzzbook.front.controller.admin.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.product.CategoryClient;
import store.buzzbook.front.common.annotation.ProductJwtValidate;
import store.buzzbook.front.dto.product.CategoryRequest;
import store.buzzbook.front.dto.product.CategoryResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/category")
public class AdminCategoryController {

	private final CategoryClient client;

	// @ProductJwtValidate
	@GetMapping
	public String adminCategoryPage(
		@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
		@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
		Model model) {

		ResponseEntity<Page<CategoryResponse>> response = client.getCategory(pageNo, pageSize);
		Page<CategoryResponse> categories = response.getBody();
		List<CategoryResponse> categoriesList = categories.getContent();

		model.addAttribute("categoryPages", categories);
		model.addAttribute("categories", categoriesList);

		return "/admin/pages/categories";
	}

	@PostMapping
	public String saveCategory(@ModelAttribute CategoryRequest category) {
		client.createCategory(category);
		return "redirect:/admin/category";
	}

	@PutMapping("/{id}")
	public String updateCategory(@ModelAttribute CategoryRequest category, @PathVariable int id) {
		client.updateCategory(id, category);
		return "redirect:/admin/category";
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteTag(@PathVariable("id") int id) {
		client.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
}
