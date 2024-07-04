package store.buzzbook.front.dto.product;

import java.time.LocalDate;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotNull;

public class ProductDirectBuyResponse {

	@NotNull
	Integer quantity;

	private int id;
	private int stock;
	private String productName;
	private String description;
	private int price;
	private LocalDate forwardDate;
	private int score;
	@Nullable
	private String thumbnailPath;
	private String stockStatus;
	private CategoryResponse category;

	//boolean 형태로 포장가능 여부 서비스 만들어 담기
}
