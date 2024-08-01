package store.buzzbook.front.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import store.buzzbook.front.dto.user.PaycoUserInfo;

@FeignClient(name = "paycoInfoClient", url = "https://apis-payco.krp.toastoven.net/payco/friends/find_member_v2.json")
public interface PaycoInfoClient {
	@PostMapping
	ResponseEntity<PaycoUserInfo> requestUserInfo(@RequestHeader("client_id") String clientId,
		@RequestHeader("access_token") String accessToken);
}
