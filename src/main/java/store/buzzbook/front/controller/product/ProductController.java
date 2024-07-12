package store.buzzbook.front.controller.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.coupon.CouponPolicyClient;
import store.buzzbook.front.client.product.CategoryClient;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.client.product.ProductTagClient;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.product.CategoryResponse;
import store.buzzbook.front.dto.product.OrderBy;
import store.buzzbook.front.dto.product.ProductDetailResponse;
import store.buzzbook.front.dto.product.ProductResponse;

@Controller
@Slf4j
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

	private static final int DEFAULT_START_PAGE = 1;
	private static final int DEFAULT_PAGE_SIZE = 5;

	private final ProductClient productClient;
	private final CouponPolicyClient couponPolicyClient;
	private final ProductTagClient productTagClient;
	private final CategoryClient categoryClient;

	@GetMapping
	public String getAllProduct(Model model,
		@RequestParam(required = false) String query,
		@RequestParam(required = false) Integer categoryId,
		@RequestParam(required = false) String orderBy,
		@RequestParam(required = false, defaultValue = DEFAULT_START_PAGE + "") int page,
		@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE + "") int size) {

		Page<ProductResponse> productPage = productClient.getAllProducts(null, query, categoryId, orderBy, page, size);
		List<ProductResponse> products = productPage.getContent();
		CategoryResponse productsCategory = categoryClient.getCategory(categoryId == null ? 0 : categoryId).getBody();

		// 각 상품에 대한 태그 가져오기
		Map<Integer, List<String>> productTagsMap = new HashMap<>();
		for (ProductResponse product : products) {
			List<String> tags = productTagClient.getTagsByProductId(product.getId())
				.getBody();        // TODO api 요청 for문안에 있는거 개선 필요해보임
			productTagsMap.put(product.getId(), tags);
		}

		model.addAttribute("products", products);
		model.addAttribute("productTagsMap", productTagsMap);
		model.addAttribute("productPage", productPage);
		model.addAttribute("categoryInfo", productsCategory);
		model.addAttribute("query", query);
		model.addAttribute("orderByList", OrderBy.values());
		model.addAttribute("orderBy", orderBy == null ? null : OrderBy.getByName(orderBy));
		model.addAttribute("page", "product");

		List<String> productType = List.of("국내도서", "해외도서", "기념품/굿즈");
		model.addAttribute("productType", productType);
		// session.setAttribute("productPage", productPage);	// 추천도서를 관련도서로 보여주기 위해 사용했던 세션

		return "index";
	}

	@GetMapping("/{id}")
	public String getProductDetail(@PathVariable("id") int id, Model model) {

		//상품정보 가져오기
		ProductDetailResponse productDetail = productClient.getProductDetail(id);
		productDetail.getBook().isProductBook();    // 해당 상품이 책인지 검사후 예외처리
		ProductResponse products = productDetail.getBook().getProduct();

		//상품정보의 카테고리 정보 가져오기
		CategoryResponse productsCategory = productDetail.getBook().getProduct().getCategory();
		int categoryId = productsCategory.getId();

		//추천상품 가져오기
		Page<ProductResponse> recommendProductPage = productClient.getAllProducts("SALE", null, categoryId, null,
			DEFAULT_START_PAGE, DEFAULT_PAGE_SIZE);

		List<CouponPolicyResponse> couponPolicies = couponPolicyClient.getSpecificCouponPolicies(id);

		CartDetailResponse cartDetailResponse = CartDetailResponse.builder()
			.productId(products.getId())
			.categoryId(productsCategory.getId())
			.productName(products.getProductName())
			.quantity(1)
			.price(products.getPrice())
			.thumbnailPath(products.getThumbnailPath())
			.canWrap(ProductResponse.isPackable(products))
			.build();

		model.addAttribute("product", productDetail.getBook().getProduct());
		model.addAttribute("book", productDetail.getBook());
		model.addAttribute("categoryInfo", productsCategory);
		model.addAttribute("reviews", productDetail.getReviews());
		model.addAttribute("recommendProducts", recommendProductPage);
		model.addAttribute("title", "상품상세");
		model.addAttribute("couponPolicies", couponPolicies);
		model.addAttribute("page", "product-detail");
		model.addAttribute("cartDetailResponse", cartDetailResponse);

		return "index";
	}

}
