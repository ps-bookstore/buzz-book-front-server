package store.buzzbook.front.service.cart.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.cart.CartClient;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.cart.CartNotFoundException;
import store.buzzbook.front.common.exception.cart.InvalidCartUuidException;
import store.buzzbook.front.common.exception.user.DeactivatedUserException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.CreateCartDetailRequest;
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
		try {
			ResponseEntity<String> responseEntity = cartClient.getUuidByUserId();
			return responseEntity.getBody();
		}catch (FeignException.Unauthorized e){
			log.debug("카트 uuid 가져오는 중 jwt 인증 실패");
			throw new AuthorizeFailException("카트 uuid 가져오는 중 jwt 인증 실패");
		}
	}

	@Override
	public String createCartAndSaveCookie(HttpServletResponse response) {
		log.debug("새로운 카트 아이디를 생성 요청합니다.");
		try {
			ResponseEntity<String> responseEntity =cartClient.createCart();
			response.addCookie(cookieUtils.wrapCartCookie(responseEntity.getBody()));
			log.debug("새로운 카트 아이디를 쿠키에 저장했습니다.");

			return responseEntity.getBody();
		}catch (FeignException.Forbidden e){
			log.debug("카트 uuid 생성 중 잘못된 UUID");
			throw new InvalidCartUuidException();
		}
	}

	@Override
	public List<CartDetailResponse> getCartByRequest(HttpServletRequest request) {
		try {
			String uuid = getCartIdFromRequest(request);
			ResponseEntity<List<CartDetailResponse>> responseEntity = cartClient.getCartByUuid(uuid);

			return responseEntity.getBody();
		}catch (FeignException.Forbidden e){
			log.debug("요청으로 카트 가져오기 중 uuid가 잘못 돼 있습니다.");
			throw new InvalidCartUuidException();
		}
	}


	@Override
	public List<CartDetailResponse> getCartByUuid(String uuid) {
		try {
			ResponseEntity<List<CartDetailResponse>> responseEntity = cartClient.getCartByUuid(uuid);

			return responseEntity.getBody();
		}catch (FeignException.Forbidden e){
			log.debug("카트 가져오기 중 uuid가 잘못 돼 있습니다.");
			throw new InvalidCartUuidException();
		}catch (FeignException.BadRequest e){
			log.debug("잘못된 cart id로의 요청입니다. : {}", uuid);
			throw new CartNotFoundException();
		}
	}

	@Override
	public List<CartDetailResponse> deleteCartDetail(String uuid, Long detailId) {
		try {
			ResponseEntity<List<CartDetailResponse>> responseEntity = cartClient.deleteCartDetail(uuid,detailId);

			return responseEntity.getBody();
		}catch (FeignException.Forbidden e){
			log.debug("카트 상세 삭제 중 uuid가 잘못 돼 있습니다.");
			throw new InvalidCartUuidException();
		}catch (FeignException.BadRequest e){
			log.debug("잘못된 id로 삭제를 요청 했습니다. : {}", detailId);
			throw new CartNotFoundException();
		}
	}

	@Override
	public void updateCart(String uuid, Long detailId, Integer quantity) {
		try {
			cartClient.updateCartDetail(uuid, detailId, quantity);
		}catch (FeignException.Forbidden e){
			log.debug("카트 상세 삭제 중 uuid가 잘못 돼 있습니다.");
			throw new InvalidCartUuidException();
		}catch (FeignException.BadRequest e){
			log.debug("카트 수정 중 카트 id 혹은 상세 id가 잘못 됐습니다. : {}", uuid);
			throw new CartNotFoundException();
		}
	}

	@Override
	public void deleteAll(String uuid) {
		try {
			cartClient.deleteAllCartDetail(uuid);
		}catch (FeignException.Forbidden e){
			log.debug("카트 전부 삭제 중 uuid가 잘못 돼 있습니다.");
			throw new InvalidCartUuidException();
		}catch (FeignException.BadRequest e){
			throw new CartNotFoundException();
		}
	}


	@Override
	public String getCartIdFromRequest(HttpServletRequest request){
		String uuid = (String)request.getAttribute(CookieUtils.COOKIE_CART_KEY);

		if(uuid == null) {
			log.debug("쿠키에서 카트 아이디를 발견할 수 없습니다.");
			throw new UnknownApiException("cart");
		}

		return uuid;
	}

	@Override
	public void createCartDetail(String uuid,CreateCartDetailRequest createCartDetailRequest) {
		try {
			cartClient.createCartDetail(uuid,createCartDetailRequest);
		} catch (FeignException.BadRequest e){
			throw new UnknownApiException("cart");
		}
	}
}
