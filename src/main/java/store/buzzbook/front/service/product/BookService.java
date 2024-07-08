package store.buzzbook.front.service.product;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.product.BookApiClient;
import store.buzzbook.front.dto.product.BookApiRequest;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookApiClient bookApiClient;

	public List<BookApiRequest.Item> searchBooks(String query) {
		return bookApiClient.searchBooks(query);
	}

	public void searchAndSaveBooks(String query) {
		bookApiClient.searchAndSaveBooks(query);
	}
}