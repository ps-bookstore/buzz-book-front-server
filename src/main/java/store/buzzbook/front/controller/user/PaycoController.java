// package store.buzzbook.front.controller.user;
//
// import java.io.IOException;
//
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
//
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import store.buzzbook.front.common.annotation.GuestOnly;
// import store.buzzbook.front.common.annotation.PaycoOauth;
// import store.buzzbook.front.common.handler.LoginSuccessHandler;
// import store.buzzbook.front.dto.user.OauthRegisterForm;
// import store.buzzbook.front.dto.user.OauthRegisterRequest;
// import store.buzzbook.front.dto.user.PaycoAuthResponse;
// import store.buzzbook.front.dto.user.PaycoUserInfo;
// import store.buzzbook.front.service.user.UserAuthService;
//
// @Slf4j
// @RequiredArgsConstructor
// @Controller
// @RequestMapping("/oauth2")
// public class PaycoController {
// 	private final UserAuthService userAuthService;
// 	private final LoginSuccessHandler loginSuccessHandler;
//
//
// 	@GuestOnly
// 	@GetMapping("/payco")
// 	public String auth() throws IOException {
// 		log.debug("OAuth try");
// 		String authRequest = userAuthService.paycoAuth();
// 		return String.format("redirect:%s", authRequest);
// 	}
//
//
// 	@GuestOnly
// 	@GetMapping("/payco/callback")
// 	public String callback(HttpServletResponse response, @RequestParam("code") String code, Model model) {
// 		log.debug("OAuth callback");
// 		PaycoAuthResponse paycoAuthResponse = userAuthService.requestPaycoToken(code);
// 		userAuthService.wrapCookie(response, paycoAuthResponse);
//
// 		PaycoUserInfo paycoUserInfo = userAuthService.getPaycoUserInfo(
// 			paycoAuthResponse.getAccessToken(),
// 			paycoAuthResponse.getRefreshToken(),
// 			response
// 		);
//
// 		// ----
// 		boolean registered = userAuthService.isRegisteredWithOauth(
// 			paycoUserInfo.getIdNo(), UserAuthService.PROVIDER_PAYCO);
// 		if (registered){
// 			return String.format("redirect:/oauth2/login?provideId=%s&provider=%s",
// 				paycoUserInfo.getIdNo(),UserAuthService.PROVIDER_PAYCO);
// 		}
//
// 		//추가정보 입력
// 		model.addAttribute("paycoUserInfo", paycoUserInfo);
// 		model.addAttribute("provider", UserAuthService.PROVIDER_PAYCO);
//
// 		return "pages/register/oauth-register";
// 	}
//
// 	@GuestOnly
// 	@PaycoOauth
// 	@PostMapping("/signup")
// 	public String signup(@Valid @ModelAttribute OauthRegisterForm oauthRegisterForm, HttpServletRequest request) {
// 		OauthRegisterRequest oauthRegisterRequest = oauthRegisterForm.toOauthRegisterRequest();
// 		PaycoUserInfo paycoUserInfo = (PaycoUserInfo) request.getAttribute(UserAuthService.PAYCO_USER_INFO);
//
// 		oauthRegisterRequest.setProvideId(paycoUserInfo.getIdNo());
// 		userAuthService.register(oauthRegisterRequest);
// 		return String.format("redirect:/oauth2/login?provideId=%s&provider=%s",
// 			oauthRegisterRequest.getProvideId(),oauthRegisterRequest.getProvider());
// 	}
//
// 	@GuestOnly
// 	@PaycoOauth
// 	@GetMapping("/login")
// 	public String login(@RequestParam("provideId") String provideId, @RequestParam("provider") String provider
// 		,HttpServletRequest request, HttpServletResponse response, Model model) throws ServletException, IOException {
//
// 		UserDetails userDetails = userAuthService.loadUserByProvideIdAndProvider(
// 			provideId, provider);
// 		UsernamePasswordAuthenticationToken authentication =
// 			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
// 		log.info("oauth login success: {}", authentication.getName());
//
// 		loginSuccessHandler.onAuthenticationSuccess(request,response,authentication);
//
// 		return "pages/error";
// 	}
//
// }
