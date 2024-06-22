package store.buzzbook.front.controller.product;

import static org.springframework.http.MediaType.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.product.ProductNotFoundException;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;

@Controller
@Slf4j
public class ProductController {

    @Autowired
    private RestClient restClient;

    // @Tag(name = "Product 관리 API", description = "상품 관련 아이템 조회 및 수정")
    @GetMapping("/product")
    public String getAllProduct(Model model) {
        List<ProductResponse> responses = null;
        try {
            responses = restClient.get()
                .uri("http://localhost:8090/api/products")
                .header(APPLICATION_JSON_VALUE)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductResponse>>() {});

        }catch (RestClientResponseException e) {
            log.error("패치 에러 Product list:", e);
            throw new ProductNotFoundException("상품 리스트 패치실패 ",e);
        }

        List<ProductRequest> products = responses.stream()
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
