package store.buzzbook.front.dto.user;

import java.io.Serial;
import java.io.Serializable;

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
public class Grade implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
	private int id;

	@NotNull
	private GradeName name;

	@NotNull
	private int standard;

	@NotNull
	private double benefit;
}