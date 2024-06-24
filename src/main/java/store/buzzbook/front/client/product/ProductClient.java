// package store.buzzbook.front.client.product;
//
// import static org.springframework.http.MediaType.*;
//
// import java.util.List;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.core.ParameterizedTypeReference;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestClient;
// import org.springframework.web.client.RestClientResponseException;
// import org.springframework.web.client.RestTemplate;
//
// import ch.qos.logback.core.joran.spi.ElementPath;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import store.buzzbook.front.common.exception.product.ProductNotFoundException;
// import store.buzzbook.front.dto.product.ProductResponse;
//
// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class ProductClient {
//
// private final RestClient restClient;
//
// 	public ResponseEntity<List<ProductResponse>> productList()
// 	{
// 		try {
// 			List<ProductResponse> responses = restClient.get()
// 				.uri("/products")
// 				.header(APPLICATION_JSON_VALUE)
// 				.retrieve()
// 				.body(new ParameterizedTypeReference<List<ProductResponse>>() {});
//
// 			return ResponseEntity.ok(responses);
//
// 		}catch (RestClientResponseException e) {
// 			log.error("패치 에러 Product list:", e);
// 			throw new ProductNotFoundException("상품 리스트 패치실패 ",e);
// 		}
//
// 	}
// }
