package store.buzzbook.front.controller.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.GetCartResponse;
import store.buzzbook.front.dto.order.CreateOrderDetailRequest;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.ReadOrdersRequest;
import store.buzzbook.front.dto.order.ReadWrappingResponse;
import store.buzzbook.front.dto.user.AddressInfo;
import store.buzzbook.front.dto.user.UserInfo;

@Controller
public class OrderController {
    @GetMapping("/order")
    public String order(Model model, HttpSession session) {
        GetCartResponse cartResponse = (GetCartResponse) session.getAttribute("cart");
        model.addAttribute("page", "order");
        model.addAttribute("title", "주문하기");
        UserInfo userInfo = UserInfo.builder().name("parkseol").email("parkseol.dev@gmail.com").contactNumber("01011111111").loginId("parkseol").build();
        model.addAttribute("myInfo", userInfo);
        List<AddressInfo> addressInfos = new ArrayList<>();
        addressInfos.add(AddressInfo.builder().id(1).addressName("우리집").build());
        model.addAttribute("addressInfos", addressInfos);
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setDeliveryPolicyId(1);
        orderRequest.setLoginId("parkseol");
        List<CreateOrderDetailRequest> details = new ArrayList<>();
        if (cartResponse != null) {
            List<CartDetailResponse> cartDetailList = cartResponse.getCartDetailList();
            for (CartDetailResponse cartDetail : cartDetailList) {
                details.add(new CreateOrderDetailRequest(cartDetail.getPrice(), cartDetail.getQuantity(), false, LocalDateTime.now(), 1, 1, null, cartDetail.getProductId(), cartDetail.getProductName(),
                    cartDetail.getThumbnailPath(), ""));
            }
        }
        orderRequest.setDetails(details);
        model.addAttribute("createOrderRequest", orderRequest);
        List<ReadWrappingResponse> packages = new ArrayList<>();
        packages.add(ReadWrappingResponse.builder().id(1).paper("없음").price(0).build());
        packages.add(ReadWrappingResponse.builder().id(2).paper("신문지").price(100).build());
        model.addAttribute("packages", packages);

        return "index";
    }

    @GetMapping("/my-page")
    public String myPage(Model model, @RequestParam int page, @RequestParam int size, HttpSession session) {
        if (page < 1) {
            page = 1;
        }
        ReadOrdersRequest orderRequest = new ReadOrdersRequest();
        orderRequest.setLoginId("parkseol");
        orderRequest.setPage(page);
        orderRequest.setSize(size);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<ReadOrdersRequest> readOrderRequestHttpEntity = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            "http://localhost:8090/api/orders/list", HttpMethod.POST, readOrderRequestHttpEntity, Map.class);

        if (response.getBody().get("total").toString().equals("0")){
            return "redirect:/my-page?page=" + (page-1) +"&size=10";
        }

        model.addAttribute("page", "mypage");

        model.addAttribute("myOrders", response.getBody().get("responseData"));
        model.addAttribute("total", response.getBody().get("total"));
        model.addAttribute("currentPage", page);

        return "index";
    }
}
