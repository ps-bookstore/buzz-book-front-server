package store.buzzbook.front.client.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "productTagClient", url = "http://${api.gateway.host}" + ":${api.gateway.port}/api/productTags")
public interface ProductTagClient {

	@GetMapping("/{productId}")
	ResponseEntity<List<String>> getTagsByProductId(@PathVariable("productId") int productId);

	@PostMapping("/{productId}/tags")
	ResponseEntity<Void> addTagToProduct(@PathVariable("productId") int productId, @RequestBody List<Integer> tagIds);

	@DeleteMapping("/{productId}/tags")
	ResponseEntity<Void> removeTagFromProduct(@PathVariable("productId") int productId, @RequestBody List<Integer> tagIds);
}