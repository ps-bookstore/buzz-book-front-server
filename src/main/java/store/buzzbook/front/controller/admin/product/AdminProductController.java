package store.buzzbook.front.controller.admin.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.common.exception.product.ProductNotFoundException;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;
import store.buzzbook.front.dto.product.ProductUpdateRequest;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/product")
public class AdminProductController {

	private final ProductClient productClient;

	@GetMapping
	public String adminPage(Model model, @RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "10") int size,
		@RequestParam(required = false) String stockStatus,
		@RequestParam(required = false) String query) {
		List<ProductRequest> products;
		Page<ProductResponse> productPage;

		if (query != null && !query.isEmpty()) {
			// 검색어가 있는 경우
			List<ProductResponse> searchResults = productClient.searchProducts(query);
			products = mapToProductRequest(searchResults);
		} else if (stockStatus != null && !stockStatus.isEmpty()) {
			// 재고 상태로 필터링
			productPage = productClient.getProductsByStockStatus(stockStatus, page, size);
			products = mapToProductRequest(productPage.getContent());
			model.addAttribute("page", productPage);
		} else {
			// 모든 상품 조회
			productPage = productClient.getAllProducts(page, size);
			products = mapToProductRequest(productPage.getContent());
			model.addAttribute("page", productPage);
		}

		model.addAttribute("products", products);
		model.addAttribute("selectedStockStatus", stockStatus);
		model.addAttribute("query", query);

		List<String> stockStatusOptions = List.of("SALE", "OUT_OF_STOCK", "SOLD_OUT");
		model.addAttribute("stockStatusOptions", stockStatusOptions);

		return "admin/pages/product-manage";
	}

	@GetMapping("/add")
	public String addProductForm() {
		return "admin/pages/product-manage-add";
	}

	@PostMapping("/add")
	public ResponseEntity<String> addProductSubmit(@ModelAttribute ProductRequest productRequest) {
		try {
			productClient.addProduct(productRequest);
			// ProductResponse productResponse = fetchProductById()
			return ResponseEntity.ok("Product added successfully");
		} catch (Exception e) {
			log.error("Error adding product", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding product");
		}
	}


	@GetMapping("/edit/{id}")
	public String editProductForm(@PathVariable("id") int id, Model model) {
		ProductResponse response = fetchProductById(id);
		ProductRequest product = mapToProductRequest(response);
		model.addAttribute("product", product);

		return "admin/pages/product-manage-edit";
	}

	@PutMapping("/edit/{id}")
	public String editProduct(@PathVariable("id") int id, @ModelAttribute ProductUpdateRequest productUpdateRequest) {
		log.info("Updating product with {}", productUpdateRequest);
		productClient.updateProduct(id, productUpdateRequest);

		return "redirect:/admin/product?page=1";
	}

	@GetMapping("/search")
	@ResponseBody
	public List<ProductResponse> searchProducts(@RequestParam String query) {
		return productClient.searchProducts(query);
	}

	private ProductResponse fetchProductById(int id) {
		try {
			return productClient.getProductById(id);
		} catch (Exception e) {
			log.error("패치 에러 Product detail:", e);
			throw new ProductNotFoundException("상품 상세 정보 패치실패 ", e);
		}
	}

	private List<ProductRequest> mapToProductRequest(List<ProductResponse> responses) {
		return responses.stream()
			.map(this::mapToProductRequest)
			.toList();
	}

	private ProductRequest mapToProductRequest(ProductResponse productResponse) {
		return ProductRequest.builder()
			// .id(productResponse.getId())
			.stock(productResponse.getStock())
			.price(productResponse.getPrice())
			.forwardDate(productResponse.getForwardDate())
			.score(productResponse.getScore())
			.thumbnailPath(productResponse.getThumbnailPath())
			.categoryId(productResponse.getCategory().getId())
			.productName(productResponse.getProductName())
			.stockStatus(productResponse.getStockStatus())
			.build();
	}
}