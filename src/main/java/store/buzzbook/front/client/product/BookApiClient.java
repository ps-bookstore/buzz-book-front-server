package store.buzzbook.front.client.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.dto.product.BookApiRequest;

@FeignClient(name = "bookApiClient", url = "http://${api.gateway.host}:${api.gateway.port}/api/books")
public interface BookApiClient {

	@GetMapping("/search")
	List<BookApiRequest.Item> searchBooks(@RequestParam(name = "query", required = false, defaultValue = "") String query);

	@PostMapping("/search")
	void searchAndSaveBooks(@RequestParam(name = "query", required = false, defaultValue = "") String query);
}