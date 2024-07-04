package store.buzzbook.front.controller.admin.product;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;
import store.buzzbook.front.dto.product.ProductUpdateForm;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/product")
public class AdminProductController {

	private final ProductClient productClient;

	@GetMapping
	public String adminPage(Model model,
		@RequestParam(required = false) String query,
		@RequestParam(required = false) String stockStatus,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "10") int size) {

		Page<ProductResponse> productPage = productClient.getAllProducts(query, stockStatus, page, size);
		List<ProductResponse> products = productPage.getContent();

		model.addAttribute("page", productPage);
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
	public String addProductSubmit(@ModelAttribute ProductRequest productRequest) {
		try {
			log.info("Adding product {}", productRequest);
			productClient.addProduct(productRequest);
			return "redirect:/admin/product?query=" + productRequest.getProductName();
		} catch (Exception e) {
			log.error("Error adding product", e);
			return "상품 추가 실패..";
		}
	}

	@GetMapping("/edit/{id}")
	public String editProductForm(@PathVariable("id") int id, Model model) {
		ProductUpdateForm product = new ProductUpdateForm(productClient.getProductById(id));
		model.addAttribute("product", product);

		return "admin/pages/product-manage-edit";
	}

	@PostMapping("/edit/{id}")
	public String editProduct(@PathVariable("id") int id, @ModelAttribute ProductUpdateForm product) {
		productClient.updateProduct(id, ProductUpdateForm.convertFormToReq(product));
		return "redirect:/admin/product?query=" + URLEncoder.encode(product.getName(), StandardCharsets.UTF_8);
		//TODO url encoder config가 필요합니다.
	}

	@GetMapping("/search")
	@ResponseBody
	public List<ProductResponse> searchProducts(@RequestParam String query) {
		return productClient.searchProducts(query);
	}

}
