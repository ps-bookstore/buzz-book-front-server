package store.buzzbook.front.controller.product;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.client.product.ProductClient;
import store.buzzbook.front.common.exception.product.ProductNotFoundException;
import store.buzzbook.front.dto.product.ProductRequest;
import store.buzzbook.front.dto.product.ProductResponse;

@Controller
@Slf4j
public class ProductController {

   @Autowired
   private ProductClient productClient;

    // @Tag(name = "Product 관리 API", description = "상품 관련 아이템 조회 및 수정")
    @GetMapping("/product")
    public String getAllProduct(Model model) {
        List<ProductResponse> responses;
        try {
            responses = productClient.getAllProducts();
        }catch (Exception e) {
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
        return "pages/product/product-list";
    }

    @GetMapping("/product/{id}")
    public String getProductDetail(@PathVariable("id") int id, Model model) {
        ProductResponse response;
        try {
            response = productClient.getProductById(id);

        }catch (Exception e) {
            log.error("패치 에러 Product list:", e);
            throw new ProductNotFoundException("상품 리스트 패치실패 ",e);
        }

        model.addAttribute("title", "상품상세");
        return "pages/product/product-detail";
    }

    //임시로 해둔 링크
    @GetMapping("/admin/con")
    public String adminTestPage(Model model)
    {
        List<ProductResponse> responses;
        try {
            responses = productClient.getAllProducts();
        } catch (Exception e) {
            log.error("패치 에러 Product list:", e);
            throw new ProductNotFoundException("상품 리스트 패치실패 ", e);
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
        return "admin/pages/product-manage";
    }
}
