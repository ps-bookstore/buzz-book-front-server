package store.buzzbook.front.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.dto.user.CreateAddressRequest;
import store.buzzbook.front.dto.user.UpdateAddressRequest;
import store.buzzbook.front.service.user.UserService;

@RestController
@RequestMapping("/mypage/address")
@RequiredArgsConstructor
public class AddressRestController {
	private final UserService userService;

	@PutMapping
	public ResponseEntity<Void> updateAddress(@RequestBody UpdateAddressRequest updateAddressRequest) {
		userService.updateAddress(updateAddressRequest);

		return ResponseEntity.ok().build();
	}

	@PostMapping
	public ResponseEntity<Void> createAddress(@RequestBody CreateAddressRequest createAddressRequest) {
		userService.createAddress(createAddressRequest);

		return ResponseEntity.ok().build();
	}


}
