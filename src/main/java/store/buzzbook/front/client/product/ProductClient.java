package store.buzzbook.front.client.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import store.buzzbook.front.dto.product.CategoryResponse;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;
import store.buzzbook.front.dto.product.ProductUpdateRequest;

@FeignClient(name = "productClient", url = "http://${api.gateway.host}" + ":${api.gateway.port}/api")
public interface ProductClient {

	@GetMapping("/products")
	Page<ProductResponse> getAllProducts(
		@RequestParam("name") String name,
		@RequestParam("status") String status,
		@RequestParam("pageNo") int pageNo,
		@RequestParam("pageSize") int pageSize);

	@PostMapping("/products")
	ProductRequest addProduct(@RequestBody ProductRequest productRequest);


	@GetMapping("/products/{id}")
	ProductResponse getProductById(@PathVariable("id") int id);

	@PutMapping("/products/{id}")
	ProductResponse updateProduct(@PathVariable("id") int id, @RequestBody ProductUpdateRequest productRequest);

	//엘라스틱 서치
	@GetMapping("/product-search/search")
	List<ProductResponse> searchProducts(@RequestParam("query") String query);

	//MySQL의 데이터를 Elasticsearch로 변환
	@GetMapping("/product-search/datainit")
	Long dataInit();

	@GetMapping("/products/categories")
	List<CategoryResponse> getAllCategories();
}
