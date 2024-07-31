package store.buzzbook.front.controller.wishlist;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
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
	public String getWishlists(@RequestParam(defaultValue = "0") int pageNo, Model model, HttpServletRequest request){

		Page<ProductResponse> wishlists = wishlistClient.getWishlist(pageNo, DEFAULT_PAGE_SIZE).getBody();

		model.addAttribute("wishlists", wishlists);

		model.addAttribute("title", "My Wishlist");
		model.addAttribute("page", "mypage-index");
		model.addAttribute("fragment", "myWishlist");

		return "index";
	}

	@PostMapping("/{productId}")
	public Boolean postWishlist(@PathVariable int productId, HttpServletRequest request){
		return wishlistClient.createWishlist(productId).getStatusCode() == HttpStatus.CREATED;
	}

	@DeleteMapping("/{id}")
	public Boolean deleteWishlist(@PathVariable long id, HttpServletRequest request){
		return wishlistClient.deleteWishlist(id).getStatusCode() == HttpStatus.NO_CONTENT;
	}

	@JwtValidate
	@GetMapping("/{productId}")
	public Boolean checkWishlist(@PathVariable int productId, HttpServletRequest request){
		return wishlistClient.checkWishlist(productId).getBody();
	}
}
