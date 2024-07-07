package store.buzzbook.front.controller.admin.product;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.product.ProductTagClient;
import store.buzzbook.front.client.product.TagClient;
import store.buzzbook.front.dto.product.TagResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/productTags")
public class AdminProductTagController {

	private final ProductTagClient productTagClient;
	private final TagClient tagClient;

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

	@PostMapping("/save")
	public String saveTags(@RequestParam int productId, @RequestParam List<Integer> tagIds) {
		// 기존 태그 삭제
		List<String> existingTags = productTagClient.getTagsByProductId(productId).getBody();
		List<TagResponse> allTags = tagClient.getAllTags().getBody();

		List<Integer> existingTagIds = existingTags.stream()
			.map(tagName -> {
				return allTags.stream()
					.filter(tag -> tag.getName().equals(tagName))
					.map(TagResponse::getId)
					.findFirst()
					.orElse(null);
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		if (!existingTagIds.isEmpty()) {
			productTagClient.removeTagFromProduct(productId, existingTagIds);
		}

		// 새로운 태그 추가
		if (!tagIds.isEmpty()) {
			productTagClient.addTagToProduct(productId, tagIds);
		}

		return "redirect:/admin/product"; // 태그 수정 후 리다이렉트 경로
	}
}
