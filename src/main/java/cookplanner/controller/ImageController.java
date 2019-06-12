package cookplanner.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cookplanner.api.ApiResponse;
import cookplanner.exception.ImageFolderExceedsThreshold;
import cookplanner.exception.ImageUploadFailedException;
import cookplanner.service.FileSystemService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/image")
public class ImageController implements IApiResponse {
	
	private final FileSystemService fileSystemService;

	public ImageController(FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
	}

	@GetMapping(produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
	public @ResponseBody byte[] getImage(@RequestParam String name) {
		log.debug("GetImage: {}", name);
		return fileSystemService.getImageFile(name);
	}
	
	@GetMapping("/list")
	public List<String> getImageUploadList() {
		return fileSystemService.getUploadFileList();
	}
	
	@GetMapping(value = "/upload-image", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
	public @ResponseBody byte[] getUploadImage(@RequestParam String name) {
		log.debug("GetImage: {}", name);
		return fileSystemService.getUploadImageFile(name);
	}
	
//	@PostMapping(value = "/update")
//	public ApiResponse<String> updateImage(@RequestParam("file") MultipartFile file) 
//			throws ImageUploadFailedException, ImageFolderExceedsThreshold {
//		String filePath = fileSystemService.saveImageFile(file);
//		return createResponse(
//				200, 
//				"Afbeelding succesvol opgeslagen", 
//				filePath);
//	}
}
