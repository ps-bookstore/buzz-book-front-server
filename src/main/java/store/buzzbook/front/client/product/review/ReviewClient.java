package store.buzzbook.front.client.product.review;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import store.buzzbook.front.dto.review.ReviewCreateRequest;
import store.buzzbook.front.dto.review.ReviewResponse;
import store.buzzbook.front.dto.review.ReviewUpdateRequest;

@FeignClient(name = "reviewClient", url = "http://${api.gateway.host}" + ":${api.gateway.port}/api/reviews")
public interface ReviewClient {

		@GetMapping
		Page<ReviewResponse> getReviews(
			@RequestParam("productId") Integer productId,
			@RequestParam("userId") Long userId,
			@RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize);

		@GetMapping("/{id}")
		ReviewResponse getReview(@PathVariable("id") int id);

		@PostMapping
		ReviewResponse createReview(@Valid @RequestBody ReviewCreateRequest request);

		@PutMapping("/{id}")
		ReviewResponse updateReview(@Valid @RequestBody ReviewUpdateRequest request);

}
