package store.buzzbook.front.dto.cart;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import store.buzzbook.front.dto.product.TagResponse;

@AllArgsConstructor
@Builder
@Getter
public class CartDetailResponse implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private long id;
	private int productId;
	private String productName;
	private int quantity;
	private int price;
	private String thumbnailPath;
	private List<TagResponse> tags;
}

