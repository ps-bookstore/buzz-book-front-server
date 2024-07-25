package store.buzzbook.front.dto.review;

import java.time.LocalDateTime;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailsWithoutReviewResponse {
	@NotNull
	private long orderDetailId;
	@NotBlank
	private LocalDateTime createAt;
	@NotNull
	private long productId;
	@NotBlank
	private String productName;
	@Nullable
	private String productThumbnailPath;
}
