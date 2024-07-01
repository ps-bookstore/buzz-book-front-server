package store.buzzbook.front.controller.admin.order;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.order.ReadOrderDetailResponse;
import store.buzzbook.front.dto.order.ReadOrdersRequest;
import store.buzzbook.front.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.front.dto.order.UpdateOrderRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {
	@GetMapping
	public String adminOrderPage(Model model, @RequestParam int page, @RequestParam int size, HttpSession session) {
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
			return "redirect:/order-manage?page=" + (page-1) +"&size=10";
		}

		model.addAttribute("page", "order-manage");

		model.addAttribute("myOrders", response.getBody().get("responseData"));
		model.addAttribute("total", response.getBody().get("total"));
		model.addAttribute("currentPage", page);

		return "index";
	}

	@GetMapping("/{id}/status")
	public String updateStatus(Model model, @PathVariable int id, @RequestParam int page, @RequestParam int size, @RequestParam String status, HttpSession session) {
		UpdateOrderRequest request = UpdateOrderRequest.builder().orderStatusName(status).id(id).loginId("parkseol").build();
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<UpdateOrderRequest> updateOrderDetailRequestHttpEntity = new HttpEntity<>(request, headers);

		ResponseEntity<ReadOrderDetailResponse> response = restTemplate.exchange(
			"http://localhost:8090/api/orders", HttpMethod.PUT, updateOrderDetailRequestHttpEntity, ReadOrderDetailResponse.class);


		ReadOrdersRequest orderRequest = new ReadOrdersRequest();
		orderRequest.setLoginId("parkseol");
		orderRequest.setPage(page);
		orderRequest.setSize(size);

		HttpEntity<ReadOrdersRequest> readOrderRequestHttpEntity = new HttpEntity<>(orderRequest, headers);

		ResponseEntity<Map> readResponse = restTemplate.exchange(
			"http://localhost:8090/api/orders/list", HttpMethod.POST, readOrderRequestHttpEntity, Map.class);

		if (readResponse.getBody().get("total").toString().equals("0")){
			return "redirect:/order-manage?page=" + (page-1) +"&size=10";
		}

		model.addAttribute("myOrders", readResponse.getBody().get("responseData"));
		model.addAttribute("total", readResponse.getBody().get("total"));
		model.addAttribute("currentPage", page);

		model.addAttribute("page", "order-manage");

		return "index";
	}

	@GetMapping("detail/{id}/status")
	public String updateDetailStatus(Model model, @PathVariable int id, @RequestParam int page, @RequestParam int size, @RequestParam String status, HttpSession session) {
		UpdateOrderDetailRequest request = UpdateOrderDetailRequest.builder().orderStatusName(status).id(id).loginId("parkseol").build();
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<UpdateOrderDetailRequest> updateOrderDetailRequestHttpEntity = new HttpEntity<>(request, headers);

		ResponseEntity<ReadOrderDetailResponse> response = restTemplate.exchange(
			"http://localhost:8090/api/orders/detail", HttpMethod.PUT, updateOrderDetailRequestHttpEntity, ReadOrderDetailResponse.class);


		ReadOrdersRequest orderRequest = new ReadOrdersRequest();
		orderRequest.setLoginId("parkseol");
		orderRequest.setPage(page);
		orderRequest.setSize(size);

		HttpEntity<ReadOrdersRequest> readOrderRequestHttpEntity = new HttpEntity<>(orderRequest, headers);

		ResponseEntity<Map> readResponse = restTemplate.exchange(
			"http://localhost:8090/api/orders/list", HttpMethod.POST, readOrderRequestHttpEntity, Map.class);

		model.addAttribute("myOrders", readResponse.getBody().get("responseData"));
		model.addAttribute("total", readResponse.getBody().get("total"));
		model.addAttribute("currentPage", page);

		model.addAttribute("page", "order-manage");

		return "index";
	}
}
