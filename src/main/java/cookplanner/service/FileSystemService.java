package cookplanner.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cookplanner.exception.ImageFolderExceedsThreshold;
import cookplanner.exception.ImageUploadFailedException;

public interface FileSystemService {
	
	public byte[] getImageFile(String name);
	
	public byte[] getUploadImageFile(String name);
	
	public List<String> getUploadFileList();
	
	public String replaceImage(String oldImage, String newImage);
	
	public String removeImage(String oldImage);
}
