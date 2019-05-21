package cookplanner.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cookplanner.api.ApiResponse;
import cookplanner.domain.IngredientName;
import cookplanner.exception.IngredientNameListEmptyException;
import cookplanner.repository.IngredientNameRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("ingredient-name")
public class IngredientNameController implements IApiResponse {
	
	private final IngredientNameRepository ingredientNameRepository;

	public IngredientNameController(IngredientNameRepository ingredientNameRepository) {
		this.ingredientNameRepository = ingredientNameRepository;
	}
	
	@GetMapping("/list")
	public ApiResponse<List<IngredientName>> getIngredientNameList() throws IngredientNameListEmptyException {
		List<IngredientName> ingredientNameList = ingredientNameRepository.findAll();
		log.debug("IngredientNameList: {}", ingredientNameList);
		if (!ingredientNameList.isEmpty()) {
			return createResponse(
					 200, 
					 "Ingredientnaam lijst succesvol opgehaald", 
					 ingredientNameList);
		}
		throw new IngredientNameListEmptyException();
	}
	
}
