package cookplanner.service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cookplanner.exception.ImageFolderExceedsThreshold;
import cookplanner.exception.ImageUploadFailedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileSystemServiceImpl implements FileSystemService {
	
	@Value("${location.images}")
    private String imageLocation;
	
	@Value("${location.images.threshold}")
	private String folderThreshold;

	@Override
	public String saveImageFile(MultipartFile file) throws ImageFolderExceedsThreshold, ImageUploadFailedException {
		log.debug("In uploadImage: {}", file.getOriginalFilename());
		Path folder = FileSystems.getDefault().getPath(imageLocation);
		Path imagePath = FileSystems.getDefault().getPath(imageLocation, file.getOriginalFilename());
		try {
			if (getFolderSize(folder) > Long.parseLong(folderThreshold)) throw new ImageFolderExceedsThreshold();
			file.transferTo(imagePath);
		} catch (IllegalStateException | IOException e) {
			throw new ImageUploadFailedException();
		}
		return imagePath.toString();
	}

	@Override
	public byte[] getImageFile(String name) {
		Path imagePath = FileSystems.getDefault().getPath(imageLocation, name);
		try {
			return Files.readAllBytes(imagePath);
		} catch(IOException e) {
			// TODO: Implement proper exception handling
			log.error("Error while getting image {} ", name);
			return null;
		}
	}
	
	private Long getFolderSize(Path folder) throws IOException {
		return Files.walk(folder)
				.filter(p -> p.toFile().isFile())
				.mapToLong(p -> p.toFile().length())
				.sum();
	}

}
