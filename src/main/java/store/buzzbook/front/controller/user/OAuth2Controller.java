package store.buzzbook.front.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class OAuth2Controller {

    // 'https://id.payco.com/oauth2.0/authorize?response_type=code
    // &client_id=3RDVSZkSYdR1p27HWM19s0K
    // &serviceProviderCode=FRIENDS
    // &redirect_uri=https%3a%2f%2f412e-220-67-223-62.ngrok-free.app%2fpayco%2flogin
    // &userLocale=ko_KR';
    private final String client_id = "&client_id=3RDVSZkSYdR1p27HWM19s0K";
    private final String serviceProviderCode = "&serviceProviderCode=FRIENDS";
    private final String redirectUrl = String.format("&redirect_uri=%s", "https%3a%2f%2f412e-220-67-223-62.ngrok-free.app%2fpayco%2flogin");
    private final String userLocale = "&userLocale=ko_KR";

    @GetMapping("/payco/window")
    public String paycoWindow() {
        String result = String.format("https://id.payco.com/oauth2.0/authorize?response_type=code%s%s%s%s", client_id, serviceProviderCode, redirectUrl, userLocale);
        log.debug("경로 확인 {}", result);
        return "redirect:" + result;
    }

    @GetMapping("/payco/login")
    public String login(@RequestParam(name = "code") String code,
                        @RequestParam(name = "state", required = false) String state,
                        @RequestParam(name = "serviceExtra", required = false) String serviceExtra) {

        StringBuilder redirectUrl = new StringBuilder("/payco/token/access?code=" + code);
        if (state != null) {
            redirectUrl.append("&state=").append(state);
        }
        if (serviceExtra != null) {
            redirectUrl.append("&serviceExtra=").append(serviceExtra);
        }

        return "redirect:" + redirectUrl.toString();
    }


    @GetMapping("/payco/token/access")
    public String accessToken(@RequestParam(name = "code") String code,
                              @RequestParam(name = "state", required = false) String state,
                              @RequestParam(name = "serviceExtra", required = false) String serviceExtra) {
        log.info("페이코 토큰 엑세스 {}", code);
        log.info("페이코 토큰 엑세스 {}", state);
        log.info("페이코 토큰 엑세스 {}", serviceExtra);

        return "redirect:/auth/login/wait";
    }


}
