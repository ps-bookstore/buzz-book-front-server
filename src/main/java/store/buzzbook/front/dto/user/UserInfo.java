package store.buzzbook.front.dto.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfo implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	private String loginId;
	private String contactNumber;
	private String name;
	private String email;
	private LocalDate birthday;
	private Grade grade;
	private boolean isAdmin;
}
