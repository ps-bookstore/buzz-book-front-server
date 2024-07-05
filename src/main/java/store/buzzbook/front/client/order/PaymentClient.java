package store.buzzbook.front.client.order;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.front.common.interceptor.FeignInterceptor;
import store.buzzbook.front.dto.payment.ReadBillLogRequest;
import store.buzzbook.front.dto.payment.ReadBillLogResponse;
import store.buzzbook.front.dto.payment.ReadBillLogWithoutOrderResponse;

@FeignClient(name = "paymentClient", url = "http://${api.gateway.host}:"
	+ "${api.gateway.port}/api/payments", configuration = FeignInterceptor.class)
public interface PaymentClient {
	@PostMapping("/bill-logs")
	ResponseEntity<List<ReadBillLogWithoutOrderResponse>> getBillLogs(@RequestBody ReadBillLogRequest readBillLogRequest);

	// @PostMapping("/admin/bill-logs")
	// ResponseEntity<?> getAllBillLogs(@RequestBody ReadBillLogsRequest readBillLogsRequest);

	@PostMapping("/bill-log")
	ResponseEntity<ReadBillLogResponse> createBillLog(@RequestBody JSONObject createBillLogRequest);
}
