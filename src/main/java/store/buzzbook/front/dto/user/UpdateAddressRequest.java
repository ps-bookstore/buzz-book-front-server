package store.buzzbook.front.dto.user;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAddressRequest(
	@NotNull
	Long id,

	@NotEmpty(message = "상세주소는 필수사항입니다.")
	@Size(max = 255,message = "255자 이내로 입력해주십시오")
	String address,

	@NotEmpty(message = "상세주소는 필수사항입니다.")
	@Size(max = 255,message = "255자 이내로 입력해주십시오")
	String detail,

	@NotNull(message = "우편번호는 필수사항입니다.")
	@Min(value = 0, message = "우편번호는 0 이하 일수 없습니다.")
	@Min(value = 99999, message = "우편번호는 99999 이상 일수 없습니다.")
	Integer zipcode,

	String nation,

	@NotEmpty(message = "별칭은 필수사항입니다.")
	@Size(max = 20, message = "별칭은 20자를 초과할 수 없습니다.")
	String alias) {
}
