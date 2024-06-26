package store.buzzbook.front.service.cart.impl;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.cart.CartClient;
import store.buzzbook.front.dto.cart.GetCartResponse;
import store.buzzbook.front.dto.cart.UpdateCartRequest;
import store.buzzbook.front.service.cart.CartService;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
	private final CartClient cartClient;

	@Override
	public GetCartResponse getCartByCartId(Long cartId) {
		ResponseEntity<GetCartResponse> responseEntity =  cartClient.getCartByCartId(cartId);

		if(Objects.equals(responseEntity.getStatusCode().value(),
			HttpStatus.BAD_REQUEST.value())){
			log.debug("잘못된 cart id로의 요청입니다. : {}", cartId);
			throw new IllegalArgumentException();
		}

		return responseEntity.getBody();
	}

	@Override
	public void deleteCartDetail(Long detailId) {
		cartClient.deleteCartDetail(detailId);
	}

	@Override
	public void updateCart(Long detailId, Integer quantity) {
		UpdateCartRequest updateCartRequest = UpdateCartRequest.builder()
			.id(detailId).quantity(quantity).build();

		cartClient.updateCartDetail(updateCartRequest);
	}
}
