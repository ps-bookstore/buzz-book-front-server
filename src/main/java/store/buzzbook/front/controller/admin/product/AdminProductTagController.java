package store.buzzbook.front.controller.admin.product;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.product.ProductTagClient;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/productTags")
public class AdminProductTagController {

	private final ProductTagClient productTagClient;

	@GetMapping("/{productId}")
	public ResponseEntity<List<String>> getTagsByProductId(@PathVariable("productId") int productId) {
		ResponseEntity<List<String>> response = productTagClient.getTagsByProductId(productId);
		return response;
	}

	@PostMapping("/{productId}/tags")
	public ResponseEntity<Void> addTagToProduct(@PathVariable("productId") int productId, @RequestBody List<Integer> tagIds) {
		ResponseEntity<Void> response = productTagClient.addTagToProduct(productId, tagIds);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{productId}/tags")
	public ResponseEntity<Void> removeTagFromProduct(@PathVariable("productId") int productId, @RequestBody List<Integer> tagIds) {
		ResponseEntity<Void> response = productTagClient.removeTagFromProduct(productId, tagIds);
		return ResponseEntity.noContent().build();
	}
}
