package store.buzzbook.front.service.cart.impl;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.cart.CartClient;
import store.buzzbook.front.common.exception.cart.CartNotFoundException;
import store.buzzbook.front.dto.cart.GetCartResponse;
import store.buzzbook.front.dto.cart.UpdateCartRequest;
import store.buzzbook.front.service.cart.CartService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
	private final CartClient cartClient;

	@Override
	public GetCartResponse getCartByCartId(Long cartId) {
		ResponseEntity<GetCartResponse> responseEntity =  cartClient.getCartByCartId(cartId);

		if(Objects.equals(responseEntity.getStatusCode().value(),
			HttpStatus.BAD_REQUEST.value())){
			log.debug("잘못된 cart id로의 요청입니다. : {}", cartId);
			throw new CartNotFoundException();
		}

		return responseEntity.getBody();
	}

	@Override
	public GetCartResponse deleteCartDetail(Long cartId, Long detailId) {
		ResponseEntity<GetCartResponse> responseEntity =  cartClient.deleteCartDetail(cartId,detailId);

		if(responseEntity.getStatusCode().value() != HttpStatus.OK.value()){
			log.debug("잘못된 id로 삭제를 요청 했습니다. : {}", detailId);
			throw new CartNotFoundException();
		}

		return responseEntity.getBody();
	}

	@Override
	public GetCartResponse updateCart(Long detailId, Integer quantity, Long cartId) {
		UpdateCartRequest updateCartRequest = UpdateCartRequest.builder()
			.id(detailId).quantity(quantity).cartId(cartId).build();

		ResponseEntity<GetCartResponse> responseEntity = cartClient.updateCartDetail(updateCartRequest);

		if(responseEntity.getStatusCode().value() != HttpStatus.OK.value()){
			log.debug("카트 수정 중 카트 id 혹은 상세 id가 잘못 됐습니다. : {}", cartId);
			throw new CartNotFoundException();
		}

		return responseEntity.getBody();
	}

	@Override
	public void deleteAll(Long cartId) {
		ResponseEntity<Void> responseEntity = cartClient.deleteAllCartDetail(cartId);

		if(responseEntity.getStatusCode().value() == HttpStatus.NOT_FOUND.value()){
			throw new CartNotFoundException();
		}
	}
}
