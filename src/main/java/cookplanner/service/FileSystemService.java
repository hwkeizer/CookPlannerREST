package cookplanner.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import cookplanner.exception.ImageFolderExceedsThreshold;
import cookplanner.exception.ImageUploadFailedException;

public interface FileSystemService {

	public String saveImageFile(MultipartFile file) 
			throws ImageFolderExceedsThreshold, ImageUploadFailedException;
	
	public byte[] getImageFile(String name);
}
