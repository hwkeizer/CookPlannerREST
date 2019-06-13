package cookplanner.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	
	@Value("${location.upload}")
    private String uploadLocation;
	
	@Value("${location.images.threshold}")
	private String folderThreshold;

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
	
	@Override
	public byte[] getUploadImageFile(String name) {
		Path imagePath = FileSystems.getDefault().getPath(uploadLocation, name);
		try {
			return Files.readAllBytes(imagePath);
		} catch(IOException e) {
			// TODO: Implement proper exception handling
			log.error("Error while getting image from uploads: {} ", imagePath);
			return null;
		}
	}
	
	@Override
	public List<String> getUploadFileList() {
		Path folder = FileSystems.getDefault().getPath(uploadLocation);		
		try {
			return getFolderList(folder);
		} catch (IOException e) {
			// TODO: implement proper exception handling, for now show the exception class and return empty list
			log.error("Could not fetch any uploads: {}", e.getClass());
			List<String> list = new ArrayList<>();
			return list;
		}				
	}
	
	/**
	 * This method does the physical file move when a recipe image is removed. The image is
	 * moved from the imageLocation to the uploadLocation. The file location is checked for 
	 * an existing file with the same name and will be renamed if needed.
	 * @param oldImage
	 * @return name of the file in the uploadLocation
	 */
	public String removeImage(String oldImage) {
		return moveFile(oldImage, imageLocation, uploadLocation).getName();
	}
	
	/**
	 * This method does the physical file moves when a recipe image is changed or added.
	 * The oldImage is moved back to the uploadLocation and the newImage is moved to the
	 * imageLocation. The oldImage can be null, only the new image is moved then. The file 
	 * locations are checked for existing files with the same name and will be renamed if 
	 * needed.
	 * 
	 * @param oldImage current image that will be moved back to the uploadLocation
	 * @param newImage new image that will be moved to the imageLocation
	 * @return the filename of the new file in the imageLocation
	 */
	@Override
	public String replaceImage(String oldImage, String newImage) {
		if (oldImage != null) {
			moveFile(oldImage, imageLocation, uploadLocation);
		}
		return moveFile(newImage, uploadLocation, imageLocation).getName();
	}
	
	/**
	 * Method to move a file from one location to another. The file name might be changed to 
	 * ensure the new filename is unique and will not overwrite any other file.
	 * 
	 * @param fileName filename
	 * @param oldLocation source folder
	 * @param newLocation destination folder
	 * @return The new File object (name can be changed)
	 */
	private File moveFile(String fileName, String oldLocation, String newLocation ) {
		File oldFile = FileSystems.getDefault().getPath(oldLocation, fileName).toFile();
		File newFile = getUniqueFile(newLocation, fileName);
		oldFile.renameTo(newFile);
		return newFile;
	}
	
	/**
	 * Method to check if a file can be saved on a given location. It checks for the existence 
	 * of the given filename in the given location. If it exists the name will be extended with 
	 * a random digit and checked again until a unique File can be returned. 
	 * 
	 * @param location folder to check for existing files
	 * @param fileName to check
	 * @return Unique File object
	 */
	private File getUniqueFile(String location, String fileName) {
		while (FileSystems.getDefault().getPath(location,  fileName).toFile().exists()) {
			String extension = fileName.substring(fileName.indexOf("."));
			fileName = fileName.substring(0, fileName.indexOf("."));
			fileName = fileName.concat(""+((int)(Math.random()*10)) + extension);
		}
		return FileSystems.getDefault().getPath(location, fileName).toFile();
	}
	
	private List<String> getFolderList(Path folder) throws IOException {
		return Files.walk(folder)
				.filter(p -> p.toFile().isFile())
				.map(p -> p.getFileName().toString())
				.collect(Collectors.toList());
	}
	
	private Long getFolderSize(Path folder) throws IOException {
		return Files.walk(folder)
				.filter(p -> p.toFile().isFile())
				.mapToLong(p -> p.toFile().length())
				.sum();
	}
}
