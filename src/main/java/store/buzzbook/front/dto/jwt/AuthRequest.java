package store.buzzbook.front.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
	private String loginId;
	private String role;
	private Long userId;
	private String accessToken;
	private String refreshToken;
}

