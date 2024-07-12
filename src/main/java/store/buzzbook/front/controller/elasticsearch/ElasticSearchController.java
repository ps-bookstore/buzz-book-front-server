package store.buzzbook.front.controller.elasticsearch;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.elasticsearch.ElasticSearchClient;
import store.buzzbook.front.dto.elasticsearch.ProductDocumentResponse;

@RestController
@RequestMapping("/api/product-search")
@RequiredArgsConstructor
public class ElasticSearchController {

	private final ElasticSearchClient elasticSearchClient;

	@GetMapping("/search")
	public List<ProductDocumentResponse> searchProducts(@RequestParam("query") String query) {
		return elasticSearchClient.searchProducts(query);
	}


}
