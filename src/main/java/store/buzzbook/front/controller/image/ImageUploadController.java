package store.buzzbook.front.controller.image;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import store.buzzbook.front.client.image.BackendImageClient;

@Controller
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageUploadController {

	private final BackendImageClient backendImageClient;

	@GetMapping
	public String testUpload()
	{
		return "admin/pages/upload";
	}

	@PostMapping("/upload")
	public String uploadImages(@RequestPart("files") List<MultipartFile> files,
		@RequestParam("folderPath") String folderPath) {
		backendImageClient.uploadImages(files, folderPath);
		return "redirect:/frontend/image/upload?success";
	}
}