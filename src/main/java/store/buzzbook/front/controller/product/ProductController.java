package store.buzzbook.front.controller.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.coupon.CouponPolicyClient;
import store.buzzbook.front.client.product.CategoryClient;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.client.product.ProductTagClient;
import store.buzzbook.front.common.exception.product.ProductNotFoundException;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.product.CategoryResponse;
import store.buzzbook.front.dto.product.ProductDetailResponse;
import store.buzzbook.front.dto.product.ProductResponse;

@Controller
@Slf4j
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

	private final ProductClient productClient;
	private final CouponPolicyClient couponPolicyClient;
	private final ProductTagClient productTagClient;
	private final CategoryClient categoryClient;

	@GetMapping
	public String getAllProduct(Model model,
		@RequestParam(required = false) String query,
		@RequestParam(required = false) Integer categoryId,
		@RequestParam(required = false) String orderBy,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "10") int size,
		HttpSession session) {


		Page<ProductResponse> productPage = productClient.getAllProducts(null, query, categoryId, orderBy, page, size);
		List<ProductResponse> products = productPage.getContent();

		// 각 상품에 대한 태그 가져오기
		Map<Integer, List<String>> productTagsMap = new HashMap<>();
		for (ProductResponse product : products) {
			List<String> tags = productTagClient.getTagsByProductId(product.getId()).getBody();
			productTagsMap.put(product.getId(), tags);
		}

		ResponseEntity<List<CategoryResponse>> response;
		if (categoryId == null) {
			response = categoryClient.getTopCategories();
		} else {
			response = categoryClient.getChildCategories(categoryId);
		}
		List<CategoryResponse> categoryList = response != null ? response.getBody() : List.of();

		model.addAttribute("products", products);
		model.addAttribute("productTagsMap", productTagsMap);
		model.addAttribute("productPage", productPage);
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("query", query);
		model.addAttribute("orderByList", List.of("name", "score", "reviews"));
		model.addAttribute("page", "product");

		List<String> productType = List.of("국내도서", "해외도서", "기념품/굿즈");
		model.addAttribute("productType", productType);
		session.setAttribute("productPage", productPage);

		return "index";
	}

	@GetMapping("/{id}")
	public String getProductDetail(@PathVariable("id") int id, Model model) {

		fetchProductById(id);

		List<CouponPolicyResponse> couponPolicies = couponPolicyClient.getSpecificCouponPolicies(id);

		ProductDetailResponse productDetail = productClient.getProductDetail(id);
		Page<ProductResponse> recommendProductPage = productClient.getAllProducts("SALE", "", null, null, 0, 5);

		model.addAttribute("product", productDetail.getBook().getProduct());
		model.addAttribute("book", productDetail.getBook());
		model.addAttribute("categories", productDetail.getBook().getProduct().getCategory().toList());
		model.addAttribute("reviews", productDetail.getReviews());
		model.addAttribute("recommendProducts", recommendProductPage);
		model.addAttribute("title", "상품상세");
		model.addAttribute("couponPolicies", couponPolicies);
		model.addAttribute("page", "product-detail");

		return "index";
	}

	private ProductResponse fetchProductById(int id) {
		try {
			return productClient.getProductById(id);
		} catch (Exception e) {
			log.error("패치 에러 Product detail:", e);
			throw new ProductNotFoundException("상품 상세 정보 패치실패 ", e);
		}
	}

}
