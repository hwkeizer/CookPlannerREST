package cookplanner.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cookplanner.domain.Recipe;
import cookplanner.exception.NoRecipesFoundException;
import cookplanner.repository.RecipeRepository;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/recipe")
public class RecipeController {

	private final RecipeRepository recipeRepository;

	public RecipeController(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
	}
	
	@GetMapping("/list")
	public List<Recipe> getRecipeList() throws NoRecipesFoundException {
		List<Recipe> recipes = recipeRepository.findAll();
		if (!recipes.isEmpty()) {
			return recipes;
		}
		throw new NoRecipesFoundException();
	}
	
}
