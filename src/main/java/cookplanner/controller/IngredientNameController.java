package cookplanner.controller;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cookplanner.api.ApiResponse;
import cookplanner.domain.IngredientName;
import cookplanner.exception.IngredientNameAlreadyExistsException;
import cookplanner.exception.IngredientNameDoesNotExistException;
import cookplanner.exception.IngredientNameInUseCannotBeDeletedException;
import cookplanner.exception.IngredientNameListEmptyException;
import cookplanner.exception.IngredientNameNotDeletedException;
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
	
	@PostMapping("/create")
	public ApiResponse<IngredientName> createIngredientName(@RequestBody IngredientName ingredientName) 
			throws IngredientNameAlreadyExistsException {
		if (ingredientNameRepository.findByName(ingredientName.getName()).isPresent()) {
			throw new IngredientNameAlreadyExistsException();
		}
		IngredientName ingredientNameResult = ingredientNameRepository.save(ingredientName);
		return createResponse(
				200, 
				"Ingredientnaam succesvol aangemaakt",
				ingredientNameResult);
	}
	
	@PutMapping("/update")
	public ApiResponse<IngredientName> updateIngredientName(@RequestBody IngredientName ingredientName) 
			throws IngredientNameDoesNotExistException {
		if (ingredientNameRepository.findById(ingredientName.getId()).isEmpty()) {
			throw new IngredientNameDoesNotExistException();
		}
		IngredientName ingredientNameResult = ingredientNameRepository.save(ingredientName);
		return createResponse(
				200, 
				"Ingredientnaam succesvol gewijzigd", 
				ingredientNameResult);
	}
	
	@DeleteMapping("/delete/{id}")
	public ApiResponse<String> deleteIngredientName(@PathVariable String id) 
			throws IngredientNameInUseCannotBeDeletedException, IngredientNameNotDeletedException {
		try {
			ingredientNameRepository.deleteById(Long.parseLong(id));
		} catch (DataIntegrityViolationException e) {
			throw new IngredientNameInUseCannotBeDeletedException();
		}
		if (ingredientNameRepository.existsById(Long.parseLong(id))) {
			throw new IngredientNameNotDeletedException();
		}
		return createResponse(
				200, 
				"Ingredientnaam succesvol verwijderd",
				id);
	}
	
}
