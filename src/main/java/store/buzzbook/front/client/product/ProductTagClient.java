package store.buzzbook.front.client.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "productTagClient", url = "http://${api.gateway.host}" + ":${api.gateway.port}/api")
public interface ProductTagClient {

	@GetMapping("/productTags/{productId}")
	ResponseEntity<List<String>> getTagsByProductId(@PathVariable("productId") int productId);

	@PostMapping("/productTags/{productId}/tags")
	ResponseEntity<Void> addTagToProduct(@PathVariable("productId") int productId, @RequestBody List<Integer> tagIds);

	@DeleteMapping("/productTags/{productId}/tags")
	ResponseEntity<Void> removeTagFromProduct(@PathVariable("productId") int productId, @RequestBody List<Integer> tagIds);
}