package store.buzzbook.front.controller.admin.product;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.client.product.ProductTagClient;
import store.buzzbook.front.client.product.TagClient;
import store.buzzbook.front.common.annotation.ProductJwtValidate;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;
import store.buzzbook.front.dto.product.ProductUpdateForm;
import store.buzzbook.front.dto.product.TagResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/product")
public class AdminProductController {

	private static final int DEFAULT_START_PAGE = 1;
	private static final int DEFAULT_PAGE_SIZE = 5;

	private final ProductClient productClient;
	private final ProductTagClient productTagClient;
	private final TagClient tagClient;

	@ProductJwtValidate
	@GetMapping
	public String adminPage(Model model,
		@RequestParam(required = false) String stockStatus,
		@RequestParam(required = false) String query,
		@RequestParam(required = false, defaultValue = DEFAULT_START_PAGE+"") int page,
		@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE+"") int size) {

		Page<ProductResponse> productPage = productClient.getAllProducts(stockStatus, query, null, null, page, size);
		List<ProductResponse> products = productPage.getContent();

		model.addAttribute("page", "admin-product");
		model.addAttribute("pageable", productPage);
		model.addAttribute("products", products);
		model.addAttribute("selectedStockStatus", stockStatus);
		model.addAttribute("query", query);

		List<String> stockStatusOptions = List.of("SALE", "OUT_OF_STOCK", "SOLD_OUT");
		model.addAttribute("stockStatusOptions", stockStatusOptions);

		return "admin/index";
	}

	@ProductJwtValidate
	@GetMapping("/add")
	public String addProductForm() {
		return "admin/pages/product-manage-add";
	}

	@ProductJwtValidate
	@PostMapping("/add")
	public String addProductSubmit(@ModelAttribute ProductRequest productRequest, RedirectAttributes redirectAttributes) {
		try {
			log.info("Adding product {}", productRequest);
			productClient.addProduct(productRequest);
			return "redirect:/admin/product?query=" + productRequest.getProductName();
		} catch (Exception e) {
			log.error("Error adding product", e);
			redirectAttributes.addFlashAttribute("errorMessage", "상품 추가 실패: " + e.getMessage());
			return "redirect:/admin/product/add";
		}
	}

	@ProductJwtValidate
	@GetMapping("/edit/{id}")
	public String editProductForm(@PathVariable("id") int id, Model model) {
		ProductUpdateForm product = new ProductUpdateForm(productClient.getProductById(id));
		model.addAttribute("product", product);
		model.addAttribute("page", "product-manage-edit");

		return "admin/index";
	}

	@ProductJwtValidate
	@PostMapping("/edit/{id}")
	public String editProduct(@PathVariable("id") int id, @ModelAttribute ProductUpdateForm product) {
		productClient.updateProduct(id, ProductUpdateForm.convertFormToReq(product));
		return "redirect:/admin/product?query=" + URLEncoder.encode(product.getName(), StandardCharsets.UTF_8);
	}

	@ProductJwtValidate
	@GetMapping("/search")
	@ResponseBody
	public List<ProductResponse> searchProducts(@RequestParam String query) {
		return productClient.searchProducts(query);
	}

	@ProductJwtValidate
	@GetMapping("/tags/{productId}")
	public String editProductTags(@PathVariable("productId") int productId, Model model) {
		ResponseEntity<List<String>> response = productTagClient.getTagsByProductId(productId);
		List<String> productTags = response.getBody();

		ResponseEntity<List<TagResponse>> allTagsResponse = tagClient.getAllTags();
		List<TagResponse> allTags = allTagsResponse.getBody();

		model.addAttribute("productId", productId);
		model.addAttribute("productTags", productTags);
		model.addAttribute("allTags", allTags);

		return "admin/pages/product-manage-tags";
	}
}
