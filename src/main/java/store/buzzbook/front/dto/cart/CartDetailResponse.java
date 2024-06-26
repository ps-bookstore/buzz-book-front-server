package store.buzzbook.front.dto.cart;

public record CartDetailResponse(
	long id,
	int productId,
	String productName,
	int quantity,
	int price,
	String thumbnailPath
) {
}
