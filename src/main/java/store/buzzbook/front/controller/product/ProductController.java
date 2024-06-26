package store.buzzbook.front.controller.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.common.exception.product.ProductNotFoundException;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class ProductController {

	@Autowired
	private ProductClient productClient;

	@GetMapping("/product")
	public String getAllProduct(Model model) {
		List<ProductResponse> responses = fetchAllProducts();
		List<ProductRequest> products = mapToProductRequest(responses);
		model.addAttribute("products", products);
		return "pages/product/product-list";
	}

	@GetMapping("/product/{id}")
	public String getProductDetail(@PathVariable("id") int id, Model model) {
		ProductResponse response = fetchProductById(id);
		ProductRequest product = mapToProductRequest(response);
		model.addAttribute("product", product);
		model.addAttribute("title", "상품상세");
		return "pages/product/product-detail";
	}

	@GetMapping("/admin/product")
	public String adminTestPage(Model model) {
		List<ProductResponse> responses = fetchAllProducts();
		List<ProductRequest> products = mapToProductRequest(responses);
		model.addAttribute("products", products);
		return "admin/pages/product-manage";
	}

	private List<ProductResponse> fetchAllProducts() {
		try {
			Page<ProductResponse> page = productClient.getAllProducts();
			return page.stream().toList();
		} catch (Exception e) {
			log.error("패치 에러 Product list:", e);
			throw new ProductNotFoundException("상품 리스트 패치실패 ", e);
		}
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
			.id(productResponse.getId())
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
