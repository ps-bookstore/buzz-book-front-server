package store.buzzbook.front.dto.product;

import java.util.List;

import org.springframework.lang.Nullable;

import lombok.Data;
import store.buzzbook.front.dto.review.ReviewResponse;

@Data
public class ProductDetailResponse {
	private BookResponse book;
	@Nullable
	private List<ReviewResponse> reviews;


}

