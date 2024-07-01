package store.buzzbook.front.service.cart.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.cart.CartClient;
import store.buzzbook.front.common.exception.cart.CartNotFoundException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.service.cart.CartService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
	private final CartClient cartClient;
	private final CookieUtils cookieUtils;

	@Override
	public String getUuidByUserId(Long userId) {
		log.debug("회원 아이디로 카트 아이디를 가져옵니다.");

		ResponseEntity<String> responseEntity = cartClient.getUuidByUserId(userId);

		if(responseEntity.getStatusCode().isError()) {
			log.debug("카트 아이디를 가져오는 중 알 수 없는 오류가 발생했습니다. {}", responseEntity.getBody());
			throw new UnknownApiException("cart");
		}

		return responseEntity.getBody();
	}

	@Override
	public String createCartAndSaveCookie(HttpServletResponse response) {
		log.debug("새로운 카트 아이디를 생성 요청합니다.");
		ResponseEntity<String> responseEntity = cartClient.createCart();

		if(responseEntity.getStatusCode().isError()) {
			log.debug("새로운 카트 생성 중 알 수 없는 오류가 발생했습니다. {}", responseEntity.getBody());
			throw new UnknownApiException("cart");
		}

		response.addCookie(cookieUtils.wrapCartCookie(responseEntity.getBody()));
		log.debug("새로운 카트 아이디를 쿠키에 저장했습니다.");

		return responseEntity.getBody();
	}

	@Override
	public List<CartDetailResponse> getCartByRequest(HttpServletRequest request) {
		String uuid = getCartIdFromRequest(request);
		ResponseEntity<List<CartDetailResponse>> responseEntity = cartClient.getCartByUuid(uuid);

		if(responseEntity.getStatusCode().is5xxServerError()) {
			log.debug("카트 아이디로 카트를 가져오는 중 오류가 발생했습니다. {}", responseEntity.getBody());
			throw new UnknownApiException("cart");
		}

		return responseEntity.getBody();
	}


	@Override
	public List<CartDetailResponse> getCartByUuid(String uuid) {
		ResponseEntity<List<CartDetailResponse>> responseEntity = cartClient.getCartByUuid(uuid);

		if(Objects.equals(responseEntity.getStatusCode().value(),
			HttpStatus.BAD_REQUEST.value())){
			log.debug("잘못된 cart id로의 요청입니다. : {}", uuid);
			throw new CartNotFoundException();
		}

		return responseEntity.getBody();
	}

	@Override
	public List<CartDetailResponse> deleteCartDetail(String uuid, Long detailId) {
		ResponseEntity<List<CartDetailResponse>> responseEntity = cartClient.deleteCartDetail(uuid,detailId);

		if(responseEntity.getStatusCode().value() != HttpStatus.OK.value()){
			log.debug("잘못된 id로 삭제를 요청 했습니다. : {}", detailId);
			throw new CartNotFoundException();
		}

		return responseEntity.getBody();
	}

	@Override
	public void updateCart(String uuid, Long detailId, Integer quantity) {
		ResponseEntity<Void> responseEntity = cartClient.updateCartDetail(uuid, detailId, quantity);

		if(responseEntity.getStatusCode().value() != HttpStatus.OK.value()){
			log.debug("카트 수정 중 카트 id 혹은 상세 id가 잘못 됐습니다. : {}", uuid);
			throw new CartNotFoundException();
		}

	}

	@Override
	public void deleteAll(String uuid) {
		ResponseEntity<Void> responseEntity = cartClient.deleteAllCartDetail(uuid);

		if(responseEntity.getStatusCode().value() == HttpStatus.NOT_FOUND.value()){
			throw new CartNotFoundException();
		}
	}


	@Override
	public String getCartIdFromRequest(HttpServletRequest request){
		Optional<Cookie> cartCookie = cookieUtils.getCartIdFromRequest(request);

		if(cartCookie.isEmpty()) {
			log.debug("쿠키에서 카트 아이디를 발견할 수 없습니다.");
			throw new UnknownApiException("cart");
		}

		return cartCookie.get().getValue();
	}
}
