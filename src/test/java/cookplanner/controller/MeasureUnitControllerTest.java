package cookplanner.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import cookplanner.domain.Account;
import cookplanner.domain.MeasureUnit;
import cookplanner.repository.MeasureUnitRepository;
import cookplanner.security.CustomUserDetailsService;
import cookplanner.security.JWTAuthenticationEntryPoint;
import cookplanner.security.JWTTokenProvider;

@WebMvcTest(MeasureUnitController.class)
class MeasureUnitControllerTest {
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	MeasureUnitRepository measureUnitRepository;
	
	// MockBeans required for securityContext
	@MockBean CustomUserDetailsService cudService;
	@MockBean JWTAuthenticationEntryPoint jwtEntryPoint;
	@MockBean JWTTokenProvider jwtProvider;

	@Test
	@WithMockUser
	void testGetMeasureUnitList_HappyPath() throws Exception {
		// Prepare
		List<MeasureUnit> measureUnitList = new ArrayList<>();
		measureUnitList.add(getTestMeasureUnit(1L, "kilogram", "kilogram"));
		measureUnitList.add(getTestMeasureUnit(2L, "eetlepel", "eetlepels"));
		when(measureUnitRepository.findAll()).thenReturn(measureUnitList);
		
		// Execute & verify
		mockMvc.perform(get("/measure-unit/list")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Maateenheden lijst succesvol opgehaald"))
				.andExpect(jsonPath("$.result.[0].name").value("kilogram"))
				.andExpect(jsonPath("$.result.[1].pluralName").value("eetlepels"));
	}
	
	@Test
	@WithMockUser
	void testGetMeasureUnitList_EmptyList() throws Exception {
		// Prepare
		List<MeasureUnit> measureUnitList = new ArrayList<>();
		when(measureUnitRepository.findAll()).thenReturn(measureUnitList);
		
		// Execute & verify
		MvcResult result = mockMvc.perform(get("/measure-unit/list")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Geen maateenheden gevonden");
	}
	
	@Test
	@WithMockUser
	void testCreatMeasureUnit_HappyPath() throws Exception {
		// Prepare
		MeasureUnit measureUnit = getTestMeasureUnit(1L, "eetlepel", "eetlepels");
		when(measureUnitRepository.findByName(measureUnit.getName())).thenReturn(Optional.empty());
		when(measureUnitRepository.save(measureUnit)).thenReturn(measureUnit);
		
		// Execute & verify
		mockMvc.perform(post("/measure-unit/create")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(measureUnit)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Maateenheid succesvol aangemaakt"))
				.andExpect(jsonPath("$.result.name").value("eetlepel"));
	}
	
	@Test
	@WithMockUser
	void testCreatMeasureUnit_AlreadyExists() throws Exception {
		// Prepare
		MeasureUnit measureUnit = getTestMeasureUnit(1L, "eetlepel", "eetlepels");
		when(measureUnitRepository.findByName(measureUnit.getName())).thenReturn(Optional.of(measureUnit));
		
		// Execute & verify
		MvcResult result = mockMvc.perform(post("/measure-unit/create")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(measureUnit)))
				.andExpect(status().isConflict())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Maateenheid bestaat al");		
	}
	
	@Test
	@WithMockUser
	void testUpdateMeasureUnit_HappyPath() throws Exception {
		// Prepare
		MeasureUnit measureUnit = getTestMeasureUnit(1L, "etlepel", "eetlepels");
		MeasureUnit measureUnitResult = getTestMeasureUnit(1L, "eetlepel", "eetlepels");
		when(measureUnitRepository.findById(measureUnit.getId())).thenReturn(Optional.of(measureUnit));
		when(measureUnitRepository.save(measureUnitResult)).thenReturn(measureUnitResult);
		
		// Execute & verify
		mockMvc.perform(put("/measure-unit/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(measureUnitResult)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Maateenheid succesvol gewijzigd"))
				.andExpect(jsonPath("$.result.name").value("eetlepel"));				
	}
	
	@Test
	@WithMockUser
	void testUpdateMeasureUnit_NotFound() throws Exception {
		// Prepare
		MeasureUnit measureUnit = getTestMeasureUnit(1L, "eetlepel", "eetlepels");
		when(measureUnitRepository.findById(measureUnit.getId())).thenReturn(Optional.empty());
		
		// Execute & verify
		MvcResult result = mockMvc.perform(put("/measure-unit/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(measureUnit)))
				.andExpect(status().isNotFound())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Maateenheid niet gevonden");
		verify(measureUnitRepository, times(1)).findById(measureUnit.getId());
		verify(measureUnitRepository, times(0)).save(measureUnit);
	}
	
	@Test
	@WithMockUser
	void testDeleteMeasureUnit_HappyPath() throws Exception {
		// Prepare
		MeasureUnit measureUnit = getTestMeasureUnit(1L, "eetlepel", "eetlepels");
		
		// Execute & verify
		mockMvc.perform(delete("/measure-unit/delete/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Maateenheid succesvol verwijderd"))
				.andExpect(jsonPath("$.result").value("1"));
		verify(measureUnitRepository, times(1)).deleteById(measureUnit.getId());
		verify(measureUnitRepository, times(1)).existsById(measureUnit.getId());
	}
	
	@Test
	@WithMockUser
	void testDeleteMeasureUnit_NotDeleted() throws Exception {
		// Prepare
		MeasureUnit measureUnit = getTestMeasureUnit(1L, "eetlepel", "eetlepels");
		when(measureUnitRepository.existsById(measureUnit.getId())).thenReturn(true);
		
		// Execute & verify
		MvcResult result = mockMvc.perform(delete("/measure-unit/delete/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isMethodNotAllowed())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Kon maateenheid niet verwijderen");
		verify(measureUnitRepository, times(1)).deleteById(measureUnit.getId());
		verify(measureUnitRepository, times(1)).existsById(measureUnit.getId());
		
	}
	
	private MeasureUnit getTestMeasureUnit(Long id, String name, String pluralName) {
		MeasureUnit measureUnit = new MeasureUnit();
		measureUnit.setId(id);
		measureUnit.setName(name);
		measureUnit.setPluralName(pluralName);
		return measureUnit;
	}
}
