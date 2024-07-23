package store.buzzbook.front.controller.user;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.front.common.exception.user.ActivateFailException;
import store.buzzbook.front.common.interceptor.CartInterceptor;
import store.buzzbook.front.dto.user.ActivateRequest;
import store.buzzbook.front.service.jwt.JwtService;
import store.buzzbook.front.service.user.UserService;

@ActiveProfiles("test")
@WebMvcTest({ActivateController.class, ActivateRestController.class})
class ActivateControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JwtService jwtService;
	@MockBean
	private UserService userService;
	@MockBean
	private CartInterceptor cartInterceptor;
	@Autowired
	private ObjectMapper objectMapper;

	private String loginId;
	private ActivateRequest activateRequest;

	@BeforeEach
	void setUp() {
		loginId = "user123";
		activateRequest = new ActivateRequest("test-activate-token", "test1");
	}

	@Test
	@WithMockUser
	void testActivateFormSuccess() throws Exception {
		doNothing().when(jwtService).existsDormantToken(activateRequest.token());

		mockMvc.perform(get("/activate").param("token", activateRequest.token()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("title", "계정 활성화"))
			.andExpect(model().attribute("page", "activate"))
			.andExpect(model().attribute("token", activateRequest.token()));

		verify(jwtService, times(1)).existsDormantToken(activateRequest.token());
	}

	@WithMockUser
	@Test
	void testActivateFormFailure() throws Exception {
		doThrow(new ActivateFailException()).when(jwtService).existsDormantToken(activateRequest.token());

		mockMvc.perform(get("/activate").param("token", activateRequest.token()))
			.andDo(print())
			.andExpect(status().isBadRequest());

		verify(jwtService, times(1)).existsDormantToken(activateRequest.token());
	}

	@WithMockUser
	@Test
	void testActivateUserSuccess() throws Exception {
		when(jwtService.checkDormantTokenAndCode(activateRequest.token(), activateRequest.code()))
			.thenReturn(loginId);

		doNothing().when(userService).activate(loginId);

		mockMvc.perform(post("/activate").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(activateRequest)))
			.andDo(print())
			.andExpect(status().isOk());

		verify(jwtService, times(1)).checkDormantTokenAndCode(activateRequest.token(), activateRequest.code());
		verify(userService, times(1)).activate(loginId);
	}

	@WithMockUser
	@Test
	void testActivateUserFailure() throws Exception {
		when(jwtService.checkDormantTokenAndCode(activateRequest.token(), activateRequest.code()))
			.thenThrow(new ActivateFailException());

		mockMvc.perform(post("/activate").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(activateRequest)))
			.andExpect(status().isBadRequest());

		verify(jwtService, times(1)).checkDormantTokenAndCode(activateRequest.token(), activateRequest.code());
		verify(userService, never()).activate(anyString());
	}
}
