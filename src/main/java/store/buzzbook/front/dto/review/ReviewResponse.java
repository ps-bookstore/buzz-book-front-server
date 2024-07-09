package store.buzzbook.front.dto.review;

import java.time.LocalDateTime;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewResponse {
	@NotBlank
	private int id;
	@NotNull
	private long userId;
	@NotBlank
	private String userName;
	@NotBlank
	private String content;
	@Nullable
	private String picturePath;
	@NotNull
	private int reviewScore;
	@NotNull
	private LocalDateTime reviewCreatedAt;
	@NotNull
	private long orderDetail;
}
