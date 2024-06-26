package store.buzzbook.front.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Grade {
	private int id;

	@NotNull
	private GradeName name;

	@NotNull
	private int standard;

	@NotNull
	private double benefit;
}