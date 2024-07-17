package store.buzzbook.front.controller.review;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.product.review.ReviewClient;
import store.buzzbook.front.dto.review.ReviewCreateRequest;
import store.buzzbook.front.dto.review.ReviewResponse;
import store.buzzbook.front.dto.review.ReviewUpdateRequest;

@Controller
@Slf4j
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewClient reviewClient;

	@GetMapping("/form")
	public String submitReviewForm2(@RequestParam long orderDetailId, Model model) {

		ReviewResponse reviewResponse = reviewClient.getAlreadyReview(orderDetailId).getBody();
		model.addAttribute("orderDetailId", orderDetailId);

		if (reviewResponse != null) {
			ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest(
				reviewResponse.getId(),
				reviewResponse.getContent(),
				reviewResponse.getReviewScore()
			);
			model.addAttribute("review", reviewUpdateRequest);
		}
		return "/admin/pages/reviewSubmit";
	}

	@PostMapping
	public ResponseEntity<Long> saveReview(@Valid @ModelAttribute ReviewCreateRequest reviewCreateRequest,
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {

		String content = reviewCreateRequest.getContent();
		int reviewScore = reviewCreateRequest.getReviewScore();
		long orderDetailId = reviewCreateRequest.getOrderDetailId();

		ReviewResponse reviewResponse = reviewClient.createReviewWithImg(content, reviewScore, orderDetailId, files)
			.getBody();

		long id = Objects.requireNonNull(reviewResponse).getProductId();

		return ResponseEntity.ok(id);
	}

	@GetMapping("/{reviewId}")
	public ReviewResponse getReview(@PathVariable int reviewId) {
		return reviewClient.getReview(reviewId).getBody();
	}

	@GetMapping("/search")
	public Page<ReviewResponse> getAllReviews(
		@RequestParam(required = false) @Parameter(description = "상품 id(long)에 해당하는 모든 리뷰 조회") Integer productId,
		@RequestParam(required = false, defaultValue = "0") @Parameter(description = "페이지 번호") Integer pageNo,
		@RequestParam(required = false, defaultValue = "5") @Parameter(description = "한 페이지에 보여질 아이템 수") Integer pageSize,
		@RequestParam(required = false) @Parameter(description = "유저 id(long)로 해당 유저가 작성한 모든 리뷰 조회") Long userId) {

		return reviewClient.getReviews(productId, userId, pageNo, pageSize).getBody();
	}

	@PutMapping("/{reviewId}")
	public ResponseEntity<Long> updateReview(@PathVariable int reviewId, @Valid @RequestBody ReviewUpdateRequest reviewReq) {

		ReviewResponse reviewResponse = reviewClient.updateReview(reviewId, reviewReq).getBody();
		long id = Objects.requireNonNull(reviewResponse).getProductId();
		return ResponseEntity.ok(id);
	}

}
