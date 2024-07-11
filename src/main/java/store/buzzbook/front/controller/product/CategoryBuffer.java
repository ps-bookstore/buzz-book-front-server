package store.buzzbook.front.controller.product;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.product.CategoryClient;
import store.buzzbook.front.dto.product.CategoryResponse;

@Component
@RequiredArgsConstructor
public class CategoryBuffer {

	private final CategoryClient categoryClient;

	@Getter
	public CategoryResponse allCategories;

	// @PostConstruct
	public void init(){
		allCategories = categoryClient.getAllCategories(0).getBody();
	}

}
