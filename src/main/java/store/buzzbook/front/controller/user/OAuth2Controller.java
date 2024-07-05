package store.buzzbook.front.controller.user;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OAuth2Controller {

    @GetMapping("/payco/login")
    public ResponseEntity<String> login(@RequestParam(name = "code") String code,
                        @RequestParam(name = "state", required = false) String state,
                        @RequestParam(name = "serviceExtra", required = false) String serviceExtra,
                        HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException {

        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        if (state != null) {
            result.put("state", state);
        }
        if (serviceExtra != null) {
            result.put("serviceExtra", serviceExtra);
        }
        log.info(result.toString());

        return new ResponseEntity<>(code,HttpStatus.OK);
    }

    @GetMapping("/payco/token/access")
    public ResponseEntity<String> accessToken(String code, String state, String serviceExtra) {
        log.debug("페이코 토큰 엑세스 {}", code);
        log.debug("페이코 토큰 엑세스 {}", state);
        log.debug("페이코 토큰 엑세스 {}", serviceExtra);

        return ResponseEntity.ok().body(code);
    }



}
