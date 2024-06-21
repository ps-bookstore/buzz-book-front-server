package store.buzzbook.front.controller.product;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;

@Controller
@Slf4j
public class ProductController {

    private final ProductClient productClient;

    public ProductController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Tag(name = "Product 관리 API", description = "상품 관련 아이템 조회 및 수정")
    @GetMapping("/product")
    public String getAllProduct(Model model) {
        List<ProductResponse> productResponses = productClient.productList();

        List<ProductRequest> products = productResponses.stream()
            .map(productResponse -> ProductRequest.builder()
                    .id(productResponse.getId())
                    .stock(productResponse.getStock())
                    .price(productResponse.getPrice())
                    .forwardDate(productResponse.getForwardDate())
                    .score(productResponse.getScore())
                    .thumbnailPath(productResponse.getThumbnailPath())
                    .categoryId(productResponse.getCategory().getId())
                    .productName(productResponse.getProductName())
                    .stockStatus(productResponse.getStockStatus())
                    .build())
            .collect(Collectors.toList());

        model.addAttribute("products", products);
        return "pages/product/productList";
    }

    @GetMapping("/detail")
    public String productDetail(Model model) {
        model.addAttribute("page", "product-detail");
        model.addAttribute("title", "상품상세");
        return "index";
    }
}
