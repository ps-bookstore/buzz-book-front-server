package store.buzzbook.front.dto.cart;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class GetCartResponse implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long userId;
	private List<CartDetailResponse> cartDetailList;
}
