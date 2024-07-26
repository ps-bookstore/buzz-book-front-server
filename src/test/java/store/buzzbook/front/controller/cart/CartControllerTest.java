package store.buzzbook.front.controller.cart;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.CreateCartDetailRequest;
import store.buzzbook.front.service.cart.CartService;

@ActiveProfiles("test")
@WebMvcTest({CartController.class, CartRestController.class})
class CartControllerTest {
	@MockBean
	private CartInterceptor cartInterceptor;
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private CartService cartService;
	@Autowired
	private ObjectMapper objectMapper;

	private String uuid;
	private Long detailId;
	private List<CartDetailResponse> cartDetailResponses;

	@BeforeEach
	void setUp() throws Exception {
		when(cartInterceptor.preHandle(any(),any(),any())).thenReturn(true);

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
				.with(csrf())
				.param("id", detailId.toString())
				.param("quantity", String.valueOf(cartDetailResponses.getFirst().getQuantity())))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/cart"));
	}

	@WithMockUser
	@Test
	void testDeleteCart() throws Exception {
		when(cartService.getCartIdFromRequest(any(HttpServletRequest.class))).thenReturn(uuid);

		mockMvc.perform(delete("/cart").with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attributeExists("cart"))
			.andExpect(model().attribute("page", "cart"))
			.andExpect(model().attribute("title", "장바구니"))
			.andExpect(model().attribute("cart", List.of()));
	}

	@WithMockUser
	@Test
	void testAddCart() throws Exception {
		CreateCartDetailRequest createCartDetailRequest = new CreateCartDetailRequest(
			10,
			2
		);
		when(cartService.getCartIdFromRequest(any(HttpServletRequest.class))).thenReturn(uuid);
		doNothing().when(cartService).createCartDetail(uuid, createCartDetailRequest);

		mockMvc.perform(post("/cart/detail").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createCartDetailRequest)))
			.andExpect(status().isOk());

		verify(cartService, times(1)).createCartDetail(anyString(), any(CreateCartDetailRequest.class));
		verify(cartService, times(1)).getCartIdFromRequest(any(HttpServletRequest.class));

	}
}