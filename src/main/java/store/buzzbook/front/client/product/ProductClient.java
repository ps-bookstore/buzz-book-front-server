package store.buzzbook.front.client.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import store.buzzbook.front.dto.product.ProductResponse;

@FeignClient(name = "productClient", url = "http://localhost:8090/api")
public interface ProductClient {

	@GetMapping("/products")
	List<ProductResponse> getAllProducts();

	@GetMapping("/products/{id}")
	ProductResponse getProductById(@PathVariable("id") int id);
}