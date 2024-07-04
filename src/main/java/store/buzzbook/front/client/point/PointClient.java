package store.buzzbook.front.client.point;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.front.common.interceptor.FeignInterceptor;
import store.buzzbook.front.dto.point.CreatePointPolicyRequest;
import store.buzzbook.front.dto.point.DeletePointPolicyRequest;
import store.buzzbook.front.dto.point.PointPolicyResponse;
import store.buzzbook.front.dto.point.UpdatePointPolicyRequest;

@FeignClient(name = "pointClient", url = "http://${api.gateway.host}:"
	+ "${api.gateway.port}/api/account/points", configuration = {FeignInterceptor.class})
public interface PointClient {

	@GetMapping
	List<PointPolicyResponse> getPointPolicies();

	@PostMapping
	PointPolicyResponse createPointPolicy(@RequestBody CreatePointPolicyRequest request);

	@PutMapping
	void updatePointPolicy(@RequestBody UpdatePointPolicyRequest request);

	@DeleteMapping
	void deletePointPolicy(@RequestBody DeletePointPolicyRequest request);
}
