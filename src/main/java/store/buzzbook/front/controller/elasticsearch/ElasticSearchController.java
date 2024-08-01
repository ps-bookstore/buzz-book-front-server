package store.buzzbook.front.controller.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.dto.elasticsearch.BookDocumentResponse;

@RestController
@RequestMapping("/api/product-search")
@RequiredArgsConstructor
public class ElasticSearchController {

	private final ProductClient productClient;

	@GetMapping("/search")
	public ResponseEntity<Page<BookDocumentResponse>> searchProducts(
		@RequestParam("query") String query,
		@RequestParam(defaultValue = "1") int pageNo,
		@RequestParam(defaultValue = "10") int pageSize) {



		return productClient.searchProducts(query, pageNo, pageSize);
	}

	@GetMapping("/datainit")
	public ResponseEntity<Long> initDataTransfer() {
		return productClient.initDataTransfer();
	}
}
