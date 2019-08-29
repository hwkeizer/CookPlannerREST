package cookplanner.service;

import java.util.List;

import cookplanner.domain.Planning;
import cookplanner.domain.Recipe;

public interface PlanBoardService {

	public List<Planning> updatePlanBoard(List<Planning> planBoard);
	
	public List<Planning> getPlanBoard();
	
	public List<Planning> addPlanning(Recipe recipe);
	
	public List<Planning> removePlanning(Long planningId);
	
	public List<Planning> updatePlanning(List<Planning> planningList);
}
