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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.product.review.ReviewClient;
import store.buzzbook.front.common.annotation.JwtValidate;
import store.buzzbook.front.dto.review.OrderDetailsWithoutReviewResponse;
import store.buzzbook.front.dto.review.ReviewRequest;
import store.buzzbook.front.dto.review.ReviewResponse;
import store.buzzbook.front.service.jwt.JwtService;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewClient reviewClient;

	@GetMapping("/form")
	public String submitReviewForm(@RequestParam long orderDetailId, Model model) {

		ReviewResponse reviewResponse = reviewClient.getAlreadyReview(orderDetailId).getBody();
		model.addAttribute("orderDetailId", orderDetailId);

		if (reviewResponse != null) {
			ReviewRequest reviewUpdateRequest = new ReviewRequest(
				reviewResponse.getContent(),
				reviewResponse.getReviewScore(),
				reviewResponse.getOrderDetailId()
			);
			model.addAttribute("review", reviewUpdateRequest);
			model.addAttribute("reviewId", reviewResponse.getId());

		}

		model.addAttribute("title", "마이페이지");
		model.addAttribute("page", "mypage-index");
		model.addAttribute("fragment", "reviewSubmit");
		return "index";
	}


	@PostMapping
	public ResponseEntity<Long> saveReview(@Valid @ModelAttribute ReviewRequest reviewRequest,
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {

		String content = reviewRequest.getContent();
		int reviewScore = reviewRequest.getReviewScore();
		long orderDetailId = reviewRequest.getOrderDetailId();

		ReviewResponse reviewResponse = reviewClient.createReview(content, reviewScore, orderDetailId, files)
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
		@RequestParam(required = false) Integer productId,
		@RequestParam(required = false, defaultValue = "0") Integer pageNo,
		@RequestParam(required = false, defaultValue = "5") Integer pageSize,
		@RequestParam(required = false) Long userId) {

		return reviewClient.getReviews(productId, userId, pageNo, pageSize).getBody();
	}

	@JwtValidate
	@GetMapping("/my-review")
	public String getMyReviews(@RequestParam(defaultValue = "0") int pageNo, Model model, HttpServletRequest request){

		long userId = (long)request.getAttribute(JwtService.USER_ID);

		Page<ReviewResponse> reviewResponses = reviewClient.getReviews(null, userId, pageNo, null).getBody();
		Page<OrderDetailsWithoutReviewResponse> noReviewODs = reviewClient.getNoReviewUserOrderDetails(userId, null, null).getBody();

		if (0 < Objects.requireNonNull(reviewResponses).getNumberOfElements()) {

			model.addAttribute("reviews", reviewResponses);
		}

		if (0 < Objects.requireNonNull(noReviewODs).getNumberOfElements()) {
			model.addAttribute("noReviewOrderDetails", noReviewODs);
		}

		model.addAttribute("title", "My Reviews");
		model.addAttribute("page", "mypage-index");
		model.addAttribute("fragment", "myReview");

		return "index";
	}


	@PutMapping("/{reviewId}")
	public ResponseEntity<Long> updateReview(@PathVariable int reviewId,
		@Valid @ModelAttribute ReviewRequest reviewReq) {

		ReviewResponse reviewResponse = reviewClient.updateReview(reviewId, reviewReq).getBody();
		long id = Objects.requireNonNull(reviewResponse).getProductId();
		return ResponseEntity.ok(id);
	}

}
