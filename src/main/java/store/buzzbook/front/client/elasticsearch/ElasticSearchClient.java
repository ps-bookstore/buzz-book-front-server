package store.buzzbook.front.client.elasticsearch;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.dto.elasticsearch.ProductDocumentResponse;

@FeignClient(name = "ProductSearchClient", url = "http://${api.gateway.host}:${api.gateway.port}/api")
public interface ElasticSearchClient
{
	@GetMapping("/product-search/search")
	List<ProductDocumentResponse> searchProducts(@RequestParam("query") String query);
}
