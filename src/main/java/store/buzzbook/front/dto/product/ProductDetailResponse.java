package store.buzzbook.front.dto.product;

import java.util.List;

import org.springframework.lang.Nullable;

import lombok.Data;
import store.buzzbook.front.dto.review.ReviewResponse;

@Data
public class ProductDetailResponse {
	@Nullable
	private BookResponse book;
	private List<ReviewResponse> reviews;
}

