package store.buzzbook.front.dto.review;

import org.springframework.lang.Nullable;

import lombok.Data;

@Data
public class ReviewCreateRequest {
	private String content;
	@Nullable
	private String picturePath;
	private int reviewScore;
	private long orderDetailId;
}
