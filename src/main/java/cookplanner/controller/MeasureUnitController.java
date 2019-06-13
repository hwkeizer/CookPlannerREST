package cookplanner.controller;

import java.sql.SQLIntegrityConstraintViolationException;
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
import cookplanner.domain.MeasureUnit;
import cookplanner.exception.MeasureUnitAlreadyExistsException;
import cookplanner.exception.MeasureUnitDoesNotExistException;
import cookplanner.exception.MeasureUnitInUseCannotBeDeleted;
import cookplanner.exception.MeasureUnitListEmptyException;
import cookplanner.exception.MeasureUnitNotDeletedException;
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
	
	@PostMapping("/create")
	public ApiResponse<MeasureUnit> createMeasureUnit(@RequestBody MeasureUnit measureUnit) 
			throws MeasureUnitAlreadyExistsException {
		if (measureUnitRepository.findByName(measureUnit.getName()).isPresent()) {
			throw new MeasureUnitAlreadyExistsException();
		}
		MeasureUnit measureUnitResult = measureUnitRepository.save(measureUnit);
		return createResponse(
				200,
				"Maateenheid succesvol aangemaakt",
				measureUnitResult);
	}
	
	@PutMapping("/update")
	public ApiResponse<MeasureUnit> updateMeasureUnit(@RequestBody MeasureUnit measureUnit) 
			throws MeasureUnitDoesNotExistException {
		if (measureUnitRepository.findById(measureUnit.getId()).isEmpty()) {
			throw new MeasureUnitDoesNotExistException();
		}
		MeasureUnit measureUnitResult = measureUnitRepository.save(measureUnit);
		return createResponse(
				200,
				"Maateenheid succesvol gewijzigd",
				measureUnitResult);
	}
	
	@DeleteMapping("/delete/{id}")
	public ApiResponse<String> deleteMeasureUnit(@PathVariable String id) 
			throws MeasureUnitNotDeletedException, MeasureUnitInUseCannotBeDeleted {
		try {
			measureUnitRepository.deleteById(Long.parseLong(id));
		} catch (DataIntegrityViolationException e) {
			throw new MeasureUnitInUseCannotBeDeleted();
		}
		if (measureUnitRepository.existsById(Long.parseLong(id))) {
			throw new MeasureUnitNotDeletedException();
		}
		return createResponse(
				200, 
				"Maateenheid succesvol verwijderd",
				id);
	}
}
