package store.buzzbook.front.client.cart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import store.buzzbook.front.dto.cart.GetCartResponse;

@FeignClient(name = "cartClient", url = "http://localhost:8080/api/cart")
public interface CartClient {

}
