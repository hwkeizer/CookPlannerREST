package cookplanner.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cookplanner.api.ApiResponse;
import cookplanner.domain.MeasureUnit;
import cookplanner.exception.MeasureUnitListEmptyException;
import cookplanner.repository.MeasureUnitRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/measure-unit")
public class MeasureUnitController implements IApiResponse {
	
	private final MeasureUnitRepository measureUnitRepository;	

	public MeasureUnitController(MeasureUnitRepository measureUnitRepository) {
		this.measureUnitRepository = measureUnitRepository;
	}

	@GetMapping("/list")
	public ApiResponse<List<MeasureUnit>> getMeasureUnitList() throws MeasureUnitListEmptyException {
		List<MeasureUnit> measureUnitList = measureUnitRepository.findAll();
		log.debug("MeasureUnitList: {}", measureUnitList);
		if (!measureUnitList.isEmpty()) {
			return createResponse(
					200,  
					"Maateenheden lijst succesvol opgehaald", 
					measureUnitList);
		}
		throw new MeasureUnitListEmptyException();
	}
}
