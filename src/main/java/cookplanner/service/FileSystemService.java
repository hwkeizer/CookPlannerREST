package cookplanner.service;

import org.springframework.web.multipart.MultipartFile;

import cookplanner.exception.ImageFolderExceedsThreshold;
import cookplanner.exception.ImageUploadFailedException;

public interface FileSystemService {

	public String saveImageFile(MultipartFile file) 
			throws ImageFolderExceedsThreshold, ImageUploadFailedException;
	
	public byte[] getImageFile(String name);
}
