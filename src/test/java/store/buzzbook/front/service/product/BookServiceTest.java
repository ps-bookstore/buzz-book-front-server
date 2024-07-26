package store.buzzbook.front.service.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import store.buzzbook.front.client.product.BookApiClient;
import store.buzzbook.front.dto.product.BookApiRequest;

class BookServiceTest {

	@Mock
	private BookApiClient bookApiClient;

	@InjectMocks
	private BookService bookService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSearchBooks() {
		// Arrange
		List<BookApiRequest.Item> expectedBooks = Arrays.asList(
			new BookApiRequest.Item(/* Initialize fields appropriately */)
		);
		when(bookApiClient.searchBooks(anyString())).thenReturn(expectedBooks);

		// Act
		List<BookApiRequest.Item> actualBooks = bookService.searchBooks("test query");

		// Assert
		verify(bookApiClient, times(1)).searchBooks("test query");
		assertEquals(expectedBooks, actualBooks);
	}

	@Test
	void testSearchAndSaveBooks() {
		// Arrange
		String query = "test query";
		doNothing().when(bookApiClient).searchAndSaveBooks(anyString());

		// Act
		bookService.searchAndSaveBooks(query);

		// Assert
		verify(bookApiClient, times(1)).searchAndSaveBooks(query);
	}
}