package store.buzzbook.front.client.jwt;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import store.buzzbook.front.dto.jwt.AuthRequest;
import store.buzzbook.front.dto.jwt.JwtResponse;
import store.buzzbook.front.service.jwt.JwtService;

@FeignClient(name = "jwtClient", url = "http://${api.gateway.host}" + ":${api.gateway.port}/api/auth")
public interface JwtClient {
    @PostMapping("/token")
    ResponseEntity<JwtResponse> authToken(@RequestBody AuthRequest authRequest);

    @GetMapping("/logout")
    ResponseEntity<Void> logout(@RequestHeader(value = JwtService.TOKEN_HEADER, required = false) String accessToken,
                                       @RequestHeader(value = JwtService.REFRESH_HEADER, required = false) String refreshToken);

    @GetMapping("/info")
    ResponseEntity<Map<String, Object>> getUserInfo(
            @RequestHeader(value = JwtService.TOKEN_HEADER, required = false) String accessToken,
            @RequestHeader(value = JwtService.REFRESH_HEADER, required = false) String refreshToken);

    @GetMapping("/dormant")
    ResponseEntity<String> getDormantToken(@RequestParam String loginId);

    @GetMapping("/activate")
    ResponseEntity<Void> existDormantToken(@RequestParam String token);

    @PutMapping("/activate")
    ResponseEntity<String> checkDormantToken(@RequestParam String token,@RequestParam String code);
}
