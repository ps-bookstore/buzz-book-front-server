package store.buzzbook.front.controller.wishlist;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.user.wishlist.WishlistClient;
import store.buzzbook.front.common.annotation.JwtValidate;
import store.buzzbook.front.dto.product.ProductResponse;

@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

	private static final int DEFAULT_PAGE_SIZE = 10;
	private final WishlistClient wishlistClient;

	@JwtValidate
	@GetMapping("/my-wishlist")
	public String getWishlists(@RequestParam(defaultValue = "0") int pageNo, Model model){

		Page<ProductResponse> wishlists = wishlistClient.getWishlist(pageNo, DEFAULT_PAGE_SIZE).getBody();

		model.addAttribute("wishlists", wishlists);

		model.addAttribute("title", "My Wishlist");
		model.addAttribute("page", "mypage-index");
		model.addAttribute("fragment", "myWishlist");

		return "index";
	}

	@PostMapping("/{productId}")
	public Long postWishlist(@PathVariable int productId){

		ResponseEntity<Long> response = wishlistClient.createWishlist(productId);

		if(response.getStatusCode() == HttpStatus.CREATED){
			return response.getBody();
		}

		return null;
	}

	@DeleteMapping("/{productId}")
	public Boolean deleteWishlist(@PathVariable int productId){
		return wishlistClient.deleteWishlist(productId).getStatusCode() == HttpStatus.NO_CONTENT;
	}

	@JwtValidate
	@GetMapping("/{productId}")
	public Long checkWishlist(@PathVariable int productId){

		ResponseEntity<Long> response = wishlistClient.checkWishlist(productId);

		if(response.getStatusCode() == HttpStatus.NO_CONTENT){
			return null;
		}
		return response.getBody();
	}
}
