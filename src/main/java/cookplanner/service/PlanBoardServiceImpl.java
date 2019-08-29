package cookplanner.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import cookplanner.domain.Planning;
import cookplanner.domain.Recipe;
import cookplanner.repository.PlanningRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlanBoardServiceImpl implements PlanBoardService {
	
	private final PlanningRepository planningRepository;	
	
	public PlanBoardServiceImpl(PlanningRepository planningRepository) {
		this.planningRepository = planningRepository;
	}

	/**
	 * Replaces the current planBoard in the database with the new one 
	 * @return the new planBoard
	 */
	@Override
	public List<Planning> updatePlanBoard(List<Planning> planBoard) {
		planningRepository.deleteAll();
		List<Planning> planBoardResult = planningRepository.saveAll(planBoard);
		Collections.sort(planBoardResult);
		return planBoardResult;
	}
	
	@Override
	public List<Planning> getPlanBoard() {
		List<Planning> planBoard = planningRepository.findAll();
		Collections.sort(planBoard);
		return planBoard;
	}

	/**
	 * Adds a new planning on the next available date with the given recipe (or
	 * the default '- no recipe planned -')
	 * @return the new planBoard
	 */
	@Override
	public List<Planning> addPlanning(Recipe recipe) {
		List<Planning> planBoard = planningRepository.findAll();
		Planning planning = new Planning(getFirstAvailableDate(planBoard));
		planning.setRecipe(recipe);
		planningRepository.save(planning);
		planBoard.add(planning);
		Collections.sort(planBoard);
		return planBoard;
	}

	/**
	 * Removes the planning from the list of plannings
	 * @return the new planBoard
	 */
	@Override
	public List<Planning> removePlanning(Long planningId) {
		planningRepository.deleteById(planningId);
		List<Planning> planBoard = planningRepository.findAll();
		Collections.sort(planBoard);
		return planBoard;
	}
	
	/**
	 * Update a list of plannings
	 */
	@Override
	public List<Planning> updatePlanning(List<Planning> planningList) {		
		return planningRepository.saveAll(planningList);		
	}
	
	private LocalDate getFirstAvailableDate(List<Planning> planBoard) {
		LocalDate localDate = LocalDate.now();
		if (planBoard.isEmpty()) return localDate;
		Collections.sort(planBoard);
		for (Planning planning : planBoard) {
			if (planning.getDate().isBefore(localDate)) continue;			
			if (!planning.getDate().equals(localDate)) {
				return localDate;
			} else localDate = localDate.plusDays(1);
		}
		return localDate;
	}
}
