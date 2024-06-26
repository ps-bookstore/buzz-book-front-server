package store.buzzbook.front.client.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;

@FeignClient(name = "productClient", url = "http://localhost:8080/api")
public interface ProductClient {

	@GetMapping("/products")
	Page<ProductResponse> getAllProducts();

	@GetMapping("/products/{id}")
	ProductResponse getProductById(@PathVariable("id") int id);

	@PutMapping("/products/{id}")
	void updateProduct(@PathVariable("id") int id, @RequestBody ProductRequest productRequest);
}
