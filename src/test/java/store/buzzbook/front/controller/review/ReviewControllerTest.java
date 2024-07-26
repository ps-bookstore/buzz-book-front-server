package store.buzzbook.front.controller.review;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import store.buzzbook.front.client.product.review.ReviewClient;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.dto.review.ReviewRequest;
import store.buzzbook.front.dto.review.ReviewResponse;
import store.buzzbook.front.service.jwt.JwtService;

@ActiveProfiles("test")
@WebMvcTest(value = ReviewController.class)
class ReviewControllerTest {

	private final long orderDetailId = 123L;
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ReviewClient reviewClient;
	@MockBean
	private CartInterceptor cartInterceptor;
	@MockBean
	private JwtService jwtService;

	private final String content = "Test review content";
	private final int score = 5;
	private final ReviewRequest mockReviewRequest = new ReviewRequest(content, score, orderDetailId);

	@Test
	@DisplayName("review form view")
	@WithMockUser
	void submitReviewFormTest() throws Exception {
		// Given
		ReviewResponse mockReviewResponse = new ReviewResponse();
		mockReviewResponse.setId(1);
		mockReviewResponse.setContent("Test review content");
		mockReviewResponse.setReviewScore(5);
		mockReviewResponse.setOrderDetailId(orderDetailId);

		when(reviewClient.getAlreadyReview(orderDetailId)).thenReturn(ResponseEntity.ok(mockReviewResponse));

		// When
		mockMvc.perform(MockMvcRequestBuilders.get("/review/form")
				.param("orderDetailId", String.valueOf(orderDetailId))
				.contentType(MediaType.APPLICATION_JSON))

			// Then
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.model().attributeExists("orderDetailId"))
			.andExpect(MockMvcResultMatchers.model().attribute("orderDetailId", orderDetailId))
			.andExpect(MockMvcResultMatchers.model().attributeExists("review"))
			.andExpect(MockMvcResultMatchers.model()
				.attribute("review", hasProperty("content", Matchers.equalTo("Test review content"))))
			.andExpect(
				MockMvcResultMatchers.model().attribute("review", hasProperty("reviewScore", Matchers.equalTo(5))))
			.andExpect(MockMvcResultMatchers.model()
				.attribute("review", hasProperty("orderDetailId", Matchers.equalTo(orderDetailId))))
			.andExpect(MockMvcResultMatchers.model().attributeExists("reviewId"))
			.andExpect(MockMvcResultMatchers.model().attribute("reviewId", Matchers.equalTo(1)))
			.andExpect(MockMvcResultMatchers.model().attribute("title", Matchers.equalTo("마이페이지")))
			.andExpect(MockMvcResultMatchers.model().attribute("page", Matchers.equalTo("mypage-index")))
			.andExpect(MockMvcResultMatchers.model().attribute("fragment", Matchers.equalTo("reviewSubmit")))
			.andExpect(MockMvcResultMatchers.view().name("index"))
			.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@DisplayName("save review - without image")
	@WithMockUser
	void saveReviewNoImageTest() throws Exception {

		long productId = 3L;


		ReviewResponse mockReviewResponse = new ReviewResponse();
		mockReviewResponse.setProductId(productId);

		when(reviewClient.createReview(content, score, orderDetailId, null)).thenReturn(
			ResponseEntity.ok(mockReviewResponse));

		mockMvc.perform(MockMvcRequestBuilders.post("/review")
				.with(csrf())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.flashAttr("reviewRequest", mockReviewRequest))

			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").value(productId));
	}

	@Test
	@DisplayName("save reivew - with image")
	@WithMockUser
	void saveReviewWithImageTest() throws Exception {

		ReviewResponse mockReviewResponse = new ReviewResponse();

		MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "file1/jpeg", "file1".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "file2/jpeg", "file2".getBytes());

		when(reviewClient.createReview(content, score, orderDetailId, List.of(file1, file2))).thenReturn(ResponseEntity.ok(mockReviewResponse));

		mockMvc.perform(MockMvcRequestBuilders
				.multipart("/review")
				.file(file1)
				.file(file2)
				.with(csrf())
				.flashAttr("reviewRequest", mockReviewRequest))


			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

	}

}
