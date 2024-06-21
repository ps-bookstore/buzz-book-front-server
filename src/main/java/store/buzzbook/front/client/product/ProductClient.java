package store.buzzbook.front.client.product;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.front.common.exception.product.ProductNotFoundException;
import store.buzzbook.front.dto.product.ProductResponse;

@Slf4j
@Component
public class ProductClient {

	private final RestClient restClient;

	public ProductClient() {
		this.restClient = RestClient.builder()
			.baseUrl("https://localhost:8080/api")
			.build();
	}

	public List<ProductResponse> productList()
	{
		try {
			return restClient.get()
				.uri("/product")
				.retrieve()
				.body(new ParameterizedTypeReference<List<ProductResponse>>() {});


		}catch (RestClientResponseException e) {
			log.error("패치 에러 Product list:", e);
			throw new ProductNotFoundException("상품 리스트 패치실패 ",e);
		}

	}
}
