package cookplanner.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cookplanner.api.ApiResponse;
import cookplanner.domain.Planning;
import cookplanner.domain.Recipe;
import cookplanner.service.PlanBoardService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/planning")
public class PlanningController implements IApiResponse {
	
	private final PlanBoardService planBoardService;

	public PlanningController(PlanBoardService planBoardService) {
	this.planBoardService = planBoardService;
}

	@GetMapping("/list")
	public ApiResponse<List<Planning>> getPlanningList() {
		List<Planning> planBoard = planBoardService.getPlanBoard();
		return createResponse(
				200, 
				"Planning succesvol opgehaald", 
				planBoard);
	}
	
	@PostMapping("/add")
	public ApiResponse<List<Planning>> addPlanning(@RequestBody Recipe recipe) {
		log.debug("RECIPE: {}", recipe);
		List<Planning> planBoard = planBoardService.addPlanning(recipe);
		return createResponse(
				200, 
				"Planning succesvol aangemaakt", 
				planBoard);
	}
	
	@PostMapping("/add-empty")
	public ApiResponse<List<Planning>> addEmptyPlanning() {
		List<Planning> planBoard = planBoardService.addPlanning(null);
		return createResponse(
				200, 
				"Lege planning succesvol aangemaakt", 
				planBoard);
	}
	
	@DeleteMapping("/delete/{id}")
	public ApiResponse<List<Planning>> deletePlanning(@PathVariable String id) {
		List<Planning> planBoard = planBoardService.removePlanning(Long.parseLong(id));
		return createResponse(
				200,
				"Planning succesvol verwijderd",
				planBoard);
	}
	
}
