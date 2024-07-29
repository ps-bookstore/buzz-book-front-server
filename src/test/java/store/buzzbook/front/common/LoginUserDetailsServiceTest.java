package store.buzzbook.front.common;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import store.buzzbook.front.dto.user.LoginUserResponse;
import store.buzzbook.front.service.user.UserService;


@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
class LoginUserDetailsServiceTest {
	@Mock
	private UserService userService;

	@InjectMocks
	private LoginUserDetailsService loginUserDetailsService;

	private LoginUserResponse loginUserResponse;
	private String password = "password";

	@BeforeEach
	void setUp() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


		loginUserResponse = new LoginUserResponse("testLoginId", passwordEncoder.encode(password), false);
	}

	@Test
	void testLoadUserByUsernameSuccess() {
		when(userService.requestLogin(loginUserResponse.loginId())).thenReturn(loginUserResponse);

		UserDetails userDetails = loginUserDetailsService.loadUserByUsername(loginUserResponse.loginId());

		assertNotNull(userDetails);
		assertEquals(loginUserResponse.loginId(), userDetails.getUsername());
		assertTrue(BCrypt.checkpw(password, userDetails.getPassword()));
		assertTrue(userDetails.getAuthorities().stream()
			.anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

		verify(userService, times(1)).requestLogin(anyString());
	}

	@Test
	void testLoadUserByUsernameAdmin() {
		loginUserResponse = new LoginUserResponse(loginUserResponse.loginId(), loginUserResponse.password(), true);
		when(userService.requestLogin(loginUserResponse.loginId())).thenReturn(loginUserResponse);

		UserDetails userDetails = loginUserDetailsService.loadUserByUsername(loginUserResponse.loginId());

		assertNotNull(userDetails);
		assertEquals(loginUserResponse.loginId(), userDetails.getUsername());
		assertTrue(BCrypt.checkpw(password, userDetails.getPassword()));
		assertTrue(userDetails.getAuthorities().stream()
			.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

		verify(userService, times(1)).requestLogin(anyString());
	}

	@Test
	void testLoadUserByUsernameUserNotFound() {
		String notExistingLoginId = "nonExistentUser";
		when(userService.requestLogin(notExistingLoginId)).thenThrow(new UsernameNotFoundException("User not found"));

		assertThrows(UsernameNotFoundException.class, () -> {
			loginUserDetailsService.loadUserByUsername(notExistingLoginId);
		});

		verify(userService, times(1)).requestLogin(anyString());
	}
}
