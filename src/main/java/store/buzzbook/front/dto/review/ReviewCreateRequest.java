package store.buzzbook.front.dto.review;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewCreateRequest {
	private String content;
	@Nullable
	private String picturePath;
	private int reviewScore;
	private long orderDetailId;
}
