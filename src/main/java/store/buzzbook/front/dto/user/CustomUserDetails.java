package store.buzzbook.front.dto.user;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {


	private final JwtLoginUser user;

	public CustomUserDetails(JwtLoginUser user) {
		this.user = user;
	}

	// role을 반환
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.role();
			}
		});
		return collection;
	}

	@Override
	public String getPassword() {
		return user.password();
	}

	@Override
	public String getUsername() {
		return user.username();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
