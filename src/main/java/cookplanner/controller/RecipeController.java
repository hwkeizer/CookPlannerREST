package cookplanner.controller;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.SizeLimitExceededException;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cookplanner.api.ApiResponse;
import cookplanner.domain.Recipe;
import cookplanner.domain.RecipeType;
import cookplanner.exception.ImageFolderExceedsThreshold;
import cookplanner.exception.ImageUploadFailedException;
import cookplanner.exception.RecipeDoesNotExistException;
import cookplanner.exception.RecipeListEmptyException;
import cookplanner.repository.RecipeRepository;
import cookplanner.service.FileSystemService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/recipe")
public class RecipeController implements IApiResponse {

	private final RecipeRepository recipeRepository;
	private final FileSystemService fileSystemService;
	
	public RecipeController(RecipeRepository recipeRepository, FileSystemService fileSystemService) {
		this.recipeRepository = recipeRepository;
		this.fileSystemService = fileSystemService;
	}

	@GetMapping("/list")
	public ApiResponse<List<Recipe>> getRecipeList() throws RecipeListEmptyException {
		List<Recipe> recipeList = recipeRepository.findAll();
		if (!recipeList.isEmpty()) {
			return createResponse(
					200, 
					"Receptenlijst succesvol opgehaald", 
					recipeList);
		}
		throw new RecipeListEmptyException();
	}
	
	@GetMapping(value = "/get-image", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
	public @ResponseBody byte[] getImage(@RequestParam String name) {
		return fileSystemService.getImageFile(name);
	}
	
	@PostMapping(value = "/upload-image")
	public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) 
			throws ImageUploadFailedException, ImageFolderExceedsThreshold {
		String filePath = fileSystemService.saveImageFile(file);
		return createResponse(
				200, 
				"Afbeelding succesvol opgeslagen", 
				filePath);
	}
	
	@GetMapping("/types")
	public ApiResponse<List<String>> getRecipeTypes() {
		List<String> types = Stream.of(RecipeType.values())
				.map(RecipeType::toString)
				.collect(Collectors.toList());
		return createResponse(
				200,
				"Recept types succesvol opgehaald",
				types);
				
	}
	
	@PutMapping("/update")
	public ApiResponse<Recipe> updateRecipe(@RequestBody Recipe recipe) throws RecipeDoesNotExistException {
		if (!recipeRepository.findById(recipe.getId()).isPresent()) {
			throw new RecipeDoesNotExistException();
		}
		Recipe recipeResult = recipeRepository.save(recipe);
		return createResponse(
				200,
				"Recept succesvol gewijzigd",
				recipeResult);
	}
	
}
