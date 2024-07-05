package store.buzzbook.front.client.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.dto.product.TagResponse;

@FeignClient(name = "TagClient", url = "http://${api.gateway.host}:${api.gateway.port}/api")
public interface TagClient {

	@PostMapping("/tags")
	ResponseEntity<TagResponse> saveTag(@RequestParam("tagName") String tagName);

	@GetMapping("/tags")
	ResponseEntity<List<TagResponse>> getAllTags(@RequestParam(value = "tagName", required = false) String tagName);

	@GetMapping("/tags")
	ResponseEntity<Page<TagResponse>> getAllTags(
		@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
		@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
		@RequestParam(value = "tagName", required = false) String tagName
	);

	@GetMapping("/tags/{id}")
	ResponseEntity<TagResponse> getTagById(@PathVariable("id") int id);

	@DeleteMapping("/tags/{id}")
	ResponseEntity<Void> deleteTag(@RequestParam("tagId") int tagId);
}