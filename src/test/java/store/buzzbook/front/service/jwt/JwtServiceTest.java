package store.buzzbook.front.service.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import feign.FeignException;
import feign.Headers;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.buzzbook.front.client.jwt.JwtClient;
import store.buzzbook.front.common.exception.auth.AuthorizeFailException;
import store.buzzbook.front.common.exception.user.ActivateFailException;
import store.buzzbook.front.common.exception.user.UnknownApiException;
import store.buzzbook.front.common.util.CookieUtils;
import store.buzzbook.front.dto.jwt.AuthRequest;
import store.buzzbook.front.dto.jwt.JwtResponse;
import store.buzzbook.front.service.jwt.impl.JwtServiceImpl;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
	@Mock
	private JwtClient jwtClient;

	@Mock
	private CookieUtils cookieUtils;

	@InjectMocks
	private JwtServiceImpl jwtService;

	private String loginId;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AuthRequest authRequest;
	private String validAccessToken;
	private String accessToken;
	private String validRefreshToken;
	private String refreshToken;
	private Cookie jwtCookie;
	private Cookie refreshCookie;
	private JwtResponse jwtResponse;
	private String dormantToken;
	private String activeCode;


	@BeforeEach
	void setUp() {
		validAccessToken = "Bearer validAccessToken";
		accessToken = "validAccessToken";
		validRefreshToken = "Bearer validRefreshToken";
		refreshToken = "validRefreshToken";
		loginId = "loginId";
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		authRequest = AuthRequest.builder()
			.userId(1L)
			.loginId("testId")
			.role("USER")
			.accessToken(validAccessToken)
			.refreshToken(validRefreshToken).build();
		jwtCookie = new Cookie(CookieUtils.COOKIE_JWT_ACCESS_KEY, validAccessToken);
		refreshCookie = new Cookie(CookieUtils.COOKIE_JWT_REFRESH_KEY, validRefreshToken);
		jwtResponse = new JwtResponse(validAccessToken, validRefreshToken);
		dormantToken = "dormantToken";
		activeCode = "activeCode";
	}

	@Test
	void testGetInfoMapFromJwtSuccess() {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.of(refreshCookie));

		Map<String, Object> body = new HashMap<>();
		body.put("userId", 1);

		HttpHeaders headers = new HttpHeaders();
		headers.add(JwtService.TOKEN_HEADER, validAccessToken);
		headers.add(JwtService.REFRESH_HEADER, validRefreshToken);

		ResponseEntity<Map<String,Object>> responseEntity = new ResponseEntity<>(body, headers, HttpStatus.OK);

		when(jwtClient.getUserInfo(anyString(), anyString())).thenReturn(responseEntity);

		Map<String, Object> result = jwtService.getInfoMapFromJwt(request, response);

		assertEquals(body, result);
		verify(jwtClient, times(1)).getUserInfo(anyString(), anyString());
	}

	@Test
	void testGetInfoMapFromJwtNull() {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.of(refreshCookie));

		when(jwtClient.getUserInfo(anyString(), anyString())).thenReturn(ResponseEntity.ok(null));

		assertThrows(AuthorizeFailException.class, () -> {
			jwtService.getInfoMapFromJwt(request, response);
		});

		verify(jwtClient, times(1)).getUserInfo(anyString(), anyString());
	}

	@Test
	void testGetInfoMapFromJwtUnauthorized() {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.of(refreshCookie));

		when(jwtClient.getUserInfo(anyString(), anyString())).thenThrow(FeignException.Unauthorized.class);

		assertThrows(AuthorizeFailException.class, () -> {
			jwtService.getInfoMapFromJwt(request, response);
		});

		verify(jwtClient, times(1)).getUserInfo(anyString(), anyString());
	}

	@Test
	void testGetUserIdFromJwtSuccess() {
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_ACCESS_KEY)).thenReturn(Optional.of(jwtCookie));
		when(cookieUtils.getCookie(request, CookieUtils.COOKIE_JWT_REFRESH_KEY)).thenReturn(Optional.of(refreshCookie));

		Map<String, Object> body = new HashMap<>();
		body.put("userId", 1);

		when(jwtClient.getUserInfo(anyString(), anyString())).thenReturn(ResponseEntity.ok(body));

		Long userId = jwtService.getUserIdFromJwt(request, response);

		assertEquals(1L, userId);
	}

	@Test
	void testAccessTokenSuccess() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JwtService.TOKEN_HEADER, validAccessToken);
		ResponseEntity<JwtResponse> responseEntity = new ResponseEntity<>(jwtResponse, headers, HttpStatus.OK);

		when(jwtClient.authToken(authRequest)).thenReturn(responseEntity);

		String token = jwtService.accessToken(authRequest);

		assertEquals(accessToken, token);
		verify(jwtClient, times(1)).authToken(authRequest);
	}

	@Test
	void testAccessTokenFailure() {
		ResponseEntity<JwtResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		when(jwtClient.authToken(authRequest)).thenReturn(responseEntity);

		assertThrowsExactly(UnknownApiException.class, () -> {
			jwtService.accessToken(authRequest);
		});

		verify(jwtClient, times(1)).authToken(authRequest);
	}

	@Test
	void testRefreshTokenSuccess() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JwtService.REFRESH_HEADER, validRefreshToken);
		ResponseEntity<JwtResponse> responseEntity = new ResponseEntity<>(jwtResponse, headers, HttpStatus.OK);

		when(jwtClient.authToken(authRequest)).thenReturn(responseEntity);

		String token = jwtService.refreshToken(authRequest);

		assertEquals(refreshToken, token);
		verify(jwtClient, times(1)).authToken(authRequest);
	}

	@Test
	void testRefreshTokenFailure() {
		when(jwtClient.authToken(authRequest)).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

		assertThrowsExactly(UnknownApiException.class, () -> {
			jwtService.refreshToken(authRequest);
		});

		verify(jwtClient, times(1)).authToken(authRequest);
	}

	@Test
	void testGetDormantTokenSuccess() {
		when(jwtClient.getDormantToken(loginId)).thenReturn(ResponseEntity.ok(dormantToken));

		String token = jwtService.getDormantToken(loginId);

		assertEquals(dormantToken, token);
		verify(jwtClient, times(1)).getDormantToken(anyString());
	}

	@Test
	void testCheckDormantTokenAndCodeSuccess() {
		when(jwtClient.checkDormantToken(dormantToken, activeCode)).thenReturn(ResponseEntity.ok(loginId));

		String result = jwtService.checkDormantTokenAndCode(dormantToken, activeCode);

		assertEquals(loginId, result);
		verify(jwtClient, times(1)).checkDormantToken(anyString(), anyString());
	}

	@Test
	void testCheckDormantTokenAndCodeNotFound() {
		when(jwtClient.checkDormantToken(anyString(), anyString())).thenThrow(FeignException.NotFound.class);

		assertThrowsExactly(ActivateFailException.class, () -> {
			jwtService.checkDormantTokenAndCode(dormantToken, activeCode);
		});

		verify(jwtClient, times(1)).checkDormantToken(anyString(), anyString());
	}

	@Test
	void testExistsDormantTokenSuccess() {
		when(jwtClient.existDormantToken(anyString())).thenReturn(ResponseEntity.ok().build());

		assertDoesNotThrow(() -> {
			jwtService.existsDormantToken(dormantToken);
		});

		verify(jwtClient, times(1)).existDormantToken(anyString());
	}

	@Test
	void testExistsDormantTokenNotFound() {
		when(jwtClient.existDormantToken(anyString())).thenThrow(FeignException.NotFound.class);

		assertThrowsExactly(ActivateFailException.class, () -> {
			jwtService.existsDormantToken(dormantToken);
		});

		verify(jwtClient, times(1)).existDormantToken(anyString());
	}
}
