package store.buzzbook.front.controller.cart;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.service.cart.CartService;

@ActiveProfiles("test")
@WebMvcTest(CartController.class)
class CartControllerTest {
	@MockBean
	private CartInterceptor cartInterceptor;
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private CartService cartService;

	private String uuid;
	private Long detailId;
	private List<CartDetailResponse> cartDetailResponses;

	@BeforeEach
	void setUp() {
		detailId = 1L;
		uuid = "sample-uuid";

		CartDetailResponse cartDetailResponse = CartDetailResponse.builder()
			.id(detailId)
			.price(1000)
			.canWrap(false)
			.categoryId(2)
			.productName("테스트 상품")
			.quantity(1)
			.thumbnailPath("/example").build();

		cartDetailResponses = new LinkedList<>();
		cartDetailResponses.add(cartDetailResponse);
	}

	@WithMockUser
	@Test
	void testGetCartByCartId() throws Exception {
		when(cartService.getCartIdFromRequest(any(HttpServletRequest.class))).thenReturn(uuid);
		when(cartService.getCartByUuid(uuid)).thenReturn(cartDetailResponses);

		mockMvc.perform(get("/cart"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attributeExists("cart"))
			.andExpect(model().attribute("page", "cart"))
			.andExpect(model().attribute("title", "장바구니"))
			.andExpect(model().attribute("cart", cartDetailResponses));
	}

	@WithMockUser
	@Test
	void testDeleteByDetailId() throws Exception {
		when(cartService.getCartIdFromRequest(any(HttpServletRequest.class))).thenReturn(uuid);
		when(cartService.deleteCartDetail(uuid, detailId)).thenReturn(cartDetailResponses);

		mockMvc.perform(get("/cart/delete")
				.param("detailId", detailId.toString()))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attributeExists("cart"))
			.andExpect(model().attribute("page", "cart"))
			.andExpect(model().attribute("title", "장바구니"))
			.andExpect(model().attribute("cart", cartDetailResponses));
	}

	@WithMockUser
	@Test
	void testUpdateCartDetail() throws Exception {
		when(cartService.getCartIdFromRequest(any(HttpServletRequest.class))).thenReturn(uuid);

		mockMvc.perform(post("/cart")
				.param("id", detailId.toString())
				.param("quantity", String.valueOf(cartDetailResponses.getFirst().getQuantity())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/cart"));
	}

	@WithMockUser
	@Test
	void testDeleteCart() throws Exception {
		when(cartService.getCartIdFromRequest(any(HttpServletRequest.class))).thenReturn(uuid);

		mockMvc.perform(delete("/cart"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attributeExists("cart"))
			.andExpect(model().attribute("page", "cart"))
			.andExpect(model().attribute("title", "장바구니"))
			.andExpect(model().attribute("cart", List.of()));
	}
}