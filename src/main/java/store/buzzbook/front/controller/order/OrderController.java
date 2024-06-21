package store.buzzbook.front.controller.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import store.buzzbook.front.dto.order.CreateOrderDetailRequest;
import store.buzzbook.front.dto.order.CreateOrderRequest;
import store.buzzbook.front.dto.order.ReadOrderResponse;
import store.buzzbook.front.dto.order.WrappingResponse;
import store.buzzbook.front.dto.payment.PaymentConfirmationRequest;
import store.buzzbook.front.dto.user.AddressInfo;
import store.buzzbook.front.dto.user.MyInfo;

@Controller
public class OrderController {
    @GetMapping("/order")
    public String order(Model model) {
        model.addAttribute("page", "order");
        model.addAttribute("title", "주문하기");
        // todo 객체 넣기
        model.addAttribute("myInfo", MyInfo.builder().name("PS").email("a@a.com").phoneNumber("11111111111").build());
        List<AddressInfo> addressInfos = new ArrayList<>();
        addressInfos.add(AddressInfo.builder().id(1).addressName("우리집").build());
        model.addAttribute("addressInfos", addressInfos);
        model.addAttribute("createOrderRequest", new CreateOrderRequest());
        // List<CreateOrderDetailRequest> createOrderDetailRequests = new ArrayList<>();
        // model.addAttribute("createOrderDetailRequestList", createOrderDetailRequests);
        List<WrappingResponse> packages = new ArrayList<>();
        packages.add(WrappingResponse.builder().paper("신문지").price(1000).build());
        model.addAttribute("packages", packages);
        model.addAttribute("paymentConfirmationRequest", new PaymentConfirmationRequest());

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
