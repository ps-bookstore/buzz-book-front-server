package store.buzzbook.front.client.image;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "ImageClient", url = "http://${api.gateway.host}:${api.gateway.port}/api/image")
public interface ImageClient {

	@PostMapping(value = "/upload", consumes = "multipart/form-data")
	ResponseEntity<String> uploadImage(
		@RequestPart("file") MultipartFile file,
		@RequestParam("folderPath") String folderPath
	);
}