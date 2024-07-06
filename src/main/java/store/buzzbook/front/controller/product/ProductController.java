package store.buzzbook.front.controller.product;

import java.util.List;

import org.springframework.data.domain.Page;
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
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.common.exception.product.ProductNotFoundException;
import store.buzzbook.front.dto.coupon.CouponPolicyResponse;
import store.buzzbook.front.dto.product.ProductResponse;

@Controller
@Slf4j
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

	private final ProductClient productClient;
	private final CouponPolicyClient couponPolicyClient;


	@GetMapping
	public String getAllProduct(Model model,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "10") int size,
		@RequestParam(required = false) String query,
		@RequestParam(required = false) String status,
		HttpSession session) {

		Page<ProductResponse> productPage = productClient.getAllProducts(query, status, page, size);
		List<ProductResponse> products = productPage.getContent();

		model.addAttribute("products", products);
		model.addAttribute("productPage", productPage);
		model.addAttribute("query", query);
		model.addAttribute("page", "product");

		List<String> productType = List.of("국내도서", "해외도서", "기념품/굿즈");
		model.addAttribute("productType", productType);
		session.setAttribute("productPage",productPage);

		return "index";
	}

	@GetMapping("/{id}")
	public String getProductDetail(@PathVariable("id") int id, Model model, HttpSession session) {
		ProductResponse product = fetchProductById(id);
		List<CouponPolicyResponse> couponPolicies = couponPolicyClient.getSpecificCouponPolicies(id);

		Page<ProductResponse> productPage = (Page<ProductResponse>) session.getAttribute("productPage");

		model.addAttribute("product", product);
		model.addAttribute("title", "상품상세");
		model.addAttribute("couponPolicies", couponPolicies);

		if (productPage != null) {
			model.addAttribute("productPage", productPage);
		}
		return "pages/product/product-detail";
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
