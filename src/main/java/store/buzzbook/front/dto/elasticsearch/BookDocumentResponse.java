package store.buzzbook.front.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDocumentResponse {

	@JsonProperty("bookId")
	private long bookId;

	@JsonProperty("productId")
	private int productId;

	@JsonProperty("isbn")
	private String isbn;

	@JsonProperty("bookTitle")
	private String bookTitle;

	@JsonProperty("description")
	private String description;

	@JsonProperty("forwardDate")
	private LocalDate forwardDate;

	@JsonProperty("authors")
	private List<String> authors;
}