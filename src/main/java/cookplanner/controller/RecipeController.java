package cookplanner.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cookplanner.api.ApiResponse;
import cookplanner.domain.Recipe;
import cookplanner.domain.RecipeType;
import cookplanner.domain.Tag;
import cookplanner.exception.RecipeDoesNotExistException;
import cookplanner.exception.RecipeListEmptyException;
import cookplanner.exception.RecipeNotDeletedException;
import cookplanner.exception.TagListEmptyException;
import cookplanner.repository.RecipeRepository;
import cookplanner.repository.TagRepository;
import cookplanner.service.FileSystemService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/recipe")
public class RecipeController implements IApiResponse {

	private final RecipeRepository recipeRepository;	
	private final TagRepository tagRepository;
	private final FileSystemService fileSystemService;
	
	public RecipeController(RecipeRepository recipeRepository, TagRepository tagRepository,
			FileSystemService fileSystemService) {
		this.recipeRepository = recipeRepository;
		this.tagRepository = tagRepository;
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
	
	@GetMapping("/all-tags")
	public ApiResponse<List<Tag>> getAllTags() throws TagListEmptyException {
		List<Tag> tagList = tagRepository.findAll();
		if (!tagList.isEmpty()) {
			return createResponse(
					200, 
					"Lijst met alle categorieën succesvol opgehaald",
					tagList);
		}
		throw new TagListEmptyException();
	}
	
	@PutMapping("/update")
	public ApiResponse<Recipe> updateRecipe(@RequestBody Recipe recipe) throws RecipeDoesNotExistException {
		if (!recipeRepository.findById(recipe.getId()).isPresent()) {
			throw new RecipeDoesNotExistException();
		}
		
		// Log output in pretty json		
		try {
			ObjectMapper mapper = new ObjectMapper();
			log.debug("Recipe updated: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(recipe.getIngredients()));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Recipe recipeResult = recipeRepository.save(recipe);
		return createResponse(
				200,
				"Recept succesvol gewijzigd",
				recipeResult);
	}
	
	/**
	 * Method to update the recipe image. This method will move the current image (if it exists) back to
	 * the upload folder and will move the new image to the image folder
	 * @param recipeId path variable that holds the recipe id
	 * @param imageName RequestBody with the name of the image
	 * @return api response
	 * @throws RecipeDoesNotExistException
	 */
	@PutMapping("{recipeId}/update-image")
	public ApiResponse<String> updateImage(@PathVariable String recipeId, @RequestBody String imageName) 
			throws RecipeDoesNotExistException {
		if (!recipeRepository.findById(Long.parseLong(recipeId)).isPresent()) {
			throw new RecipeDoesNotExistException();
		}
		Recipe recipe = recipeRepository.findById(Long.parseLong(recipeId)).get();
		String newName = fileSystemService.replaceImage(recipe.getImage(), imageName);
		recipe.setImage(newName);
		recipeRepository.save(recipe);
		return createResponse(
				200, 
				"Afbeelding succesvol gewijzigd", 
				newName);
	}
	
	@PostMapping("/create")
	public ApiResponse<Recipe> createRecipe(@RequestBody Recipe recipe) {		
		// Log output in pretty json		
		try {
			ObjectMapper mapper = new ObjectMapper();
			log.debug("Recipe created: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(recipe.getIngredients()));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Recipe recipeResult = recipeRepository.save(recipe);
		return createResponse(
				200,
				"Recept succesvol aangemaakt",
				recipeResult);
	}
	
	@DeleteMapping("/delete/{id}")
	public ApiResponse<String> deleteRecipe(@PathVariable String id) throws RecipeNotDeletedException {
		Optional<Recipe> optionalRecipe = recipeRepository.findById(Long.parseLong(id));
		if (optionalRecipe.isPresent()) {
			fileSystemService.removeImage(optionalRecipe.get().getImage());
			recipeRepository.delete(optionalRecipe.get());
		}		
		if (recipeRepository.findById(Long.parseLong(id)).isPresent()) {
			throw new RecipeNotDeletedException();
		}
		return createResponse(
				200,
				"Recept succesvol verwijderd",
				id);
	}
	
}
