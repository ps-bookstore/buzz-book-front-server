package store.buzzbook.front.controller.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import store.buzzbook.front.dto.cart.CartDetailResponse;
import store.buzzbook.front.dto.cart.GetCartResponse;
import store.buzzbook.front.dto.order.CreateOrderDetailRequest;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.order.ReadWrappingResponse;
import store.buzzbook.front.dto.product.ProductResponse;
import store.buzzbook.front.dto.user.AddressInfo;
import store.buzzbook.front.dto.user.UserInfo;

@Controller
public class OrderController {
    @GetMapping("/order")
    public String order(Model model, HttpSession session) {
        // GetCartResponse cartResponse = (GetCartResponse) session.getAttribute("cart");
        model.addAttribute("page", "order");
        model.addAttribute("title", "주문하기");
        UserInfo userInfo = UserInfo.builder().name("ps").email("testemail0000@email.net").contactNumber("01900001111").loginId("testid123123").build();
        model.addAttribute("myInfo", userInfo);
        List<AddressInfo> addressInfos = new ArrayList<>();
        addressInfos.add(AddressInfo.builder().id(1).addressName("우리집").build());
        model.addAttribute("addressInfos", addressInfos);
        CreateOrderRequest request = new CreateOrderRequest();
        request.setDeliveryPolicyId(1);
        request.setLoginId("testid123123");
        List<CreateOrderDetailRequest> details = new ArrayList<>();
        // if (cartResponse != null) {
        //     List<CartDetailResponse> cartDetailList = cartResponse.getCartDetailList();
        //     for (CartDetailResponse cartDetail : cartDetailList) {
        //         details.add(new CreateOrderDetailRequest(cartDetail.getPrice(), cartDetail.getQuantity(), false, 1, null,
        //             cartDetail.getProductId(), cartDetail.getProductName(), cartDetail.getThumbnailPath(), null));
        //     }
        // }
        request.setDetails(details);
        model.addAttribute("createOrderRequest", request);
        List<ReadWrappingResponse> packages = new ArrayList<>();
        packages.add(ReadWrappingResponse.builder().id(2).paper("신문지").price(1000).build());
        model.addAttribute("packages", packages);

        return "index";
    }

    @GetMapping("/my-page")
    public String myPage(Model model) {
        Page<ReadOrderResponse> readOrderResponses = new PageImpl<>(new ArrayList<>());
        model.addAttribute("page", "my-page");
        model.addAttribute("myOrders", readOrderResponses);

        return "index";
    }
}
