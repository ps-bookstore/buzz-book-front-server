package store.buzzbook.front.controller.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.image.ImageClient;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageUploadController {

	private final ImageClient imageClient;

	@GetMapping
	public String testUpload()
	{
		return "admin/page/upload";
	}

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(
		@RequestPart("file") MultipartFile file,
		@RequestParam("folderPath") String folderPath
	) {
		return imageClient.uploadImage(file, folderPath);
	}
}