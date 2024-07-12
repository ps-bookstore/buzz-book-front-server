package store.buzzbook.front.client.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.dto.product.CategoryRequest;
import store.buzzbook.front.dto.product.CategoryResponse;

@FeignClient(name = "CategoryClient", url = "http://${api.gateway.host}:${api.gateway.port}/api/categories")
public interface CategoryClient {

	@GetMapping("/{id}")
	ResponseEntity<CategoryResponse> getCategory(@PathVariable("id") int id);

	@GetMapping
	ResponseEntity<Page<CategoryResponse>> getCategory(
		@RequestParam(name = "pageNo", required = false) Integer pageNo,
		@RequestParam(name = "pageSize", required = false) Integer pageSize);

	@PostMapping
	ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest);

	@PutMapping("/{id}")
	ResponseEntity<CategoryResponse> updateCategory(@PathVariable("id") int id, @RequestBody CategoryRequest categoryRequest);

	@DeleteMapping("/{id}")
	ResponseEntity<Void> deleteCategory(@PathVariable("id") int id);
}
