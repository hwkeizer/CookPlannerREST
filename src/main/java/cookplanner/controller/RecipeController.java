package cookplanner.controller;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cookplanner.api.ApiResponse;
import cookplanner.domain.Recipe;
import cookplanner.exception.RecipeListEmptyException;
import cookplanner.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/recipe")
public class RecipeController implements IApiResponse {
	
	@Value("${location.images}")
    private String imageLocation;

	private final RecipeRepository recipeRepository;

	public RecipeController(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
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
	
	@GetMapping(value = "/get-image", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody byte[] getImage(@RequestParam String name) throws IOException {
		Path imagePath = FileSystems.getDefault().getPath(imageLocation, name);
		return Files.readAllBytes(imagePath);
	}
	
}
