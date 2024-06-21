package store.buzzbook.front.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MyInfo {
	private String name;
	private String phoneNumber;
	private String email;
}
