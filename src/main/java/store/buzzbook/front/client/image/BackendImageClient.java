package store.buzzbook.front.client.image;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "backendImageClient", url = "http://${api.gateway.host}:${api.gateway.port}/api/image")
public interface BackendImageClient {

	@PostMapping(value = "/upload", consumes = "multipart/form-data")
	ResponseEntity<String> uploadImages(@RequestPart("files") List<MultipartFile> files,
		@RequestParam("folderPath") String folderPath);
}