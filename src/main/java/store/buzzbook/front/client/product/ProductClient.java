package store.buzzbook.front.client.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;

@FeignClient(name = "productClient", url = "http://localhost:8090/api")
public interface ProductClient {

	@GetMapping("/products")
	List<ProductResponse> getAllProducts();

	@GetMapping("/products/{id}")
	ProductResponse getProductById(@PathVariable("id") int id);

	@PutMapping("/products/{id}")
	void updateProduct(@PathVariable("id") int id, @RequestBody ProductRequest productRequest);
}