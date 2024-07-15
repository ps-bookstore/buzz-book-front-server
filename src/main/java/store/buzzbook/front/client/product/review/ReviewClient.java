package store.buzzbook.front.client.product.review;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import store.buzzbook.front.common.config.FeignConfig;
import store.buzzbook.front.dto.review.ReviewCreateRequest;
import store.buzzbook.front.dto.review.ReviewResponse;
import store.buzzbook.front.dto.review.ReviewUpdateRequest;

@FeignClient(name = "reviewClient", url = "http://${api.gateway.host}:${api.gateway.port}/api/reviews", configuration = FeignConfig.class)
public interface ReviewClient {

	@GetMapping
	ResponseEntity<Page<ReviewResponse>> getReviews(
		@RequestParam("productId") Integer productId,
		@RequestParam("userId") Long userId,
		@RequestParam("pageNo") Integer pageNo,
		@RequestParam("pageSize") Integer pageSize);

	@GetMapping("/{reviewId}")
	ResponseEntity<ReviewResponse> getReview(@PathVariable("reviewId") int id);

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<ReviewResponse> createReviewWithImg(
		@RequestPart("reviewCreateRequest") ReviewCreateRequest reviewCreateRequest,
		@RequestPart(value = "files", required = false) MultipartFile file);
	//사진리뷰는 처음 작성시에만 사진 업로드가 가능하고 수정할땐 사진 업로드, 수정 불가능

	@PostMapping
	ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewCreateRequest request);

	@PutMapping("/{id}")
	ResponseEntity<ReviewResponse> updateReview(@PathVariable("id") int id,
		@Valid @RequestBody ReviewUpdateRequest request);

}
