package cookplanner.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import cookplanner.domain.Account;
import cookplanner.domain.MeasureUnit;
import cookplanner.repository.MeasureUnitRepository;
import cookplanner.security.CustomUserDetailsService;
import cookplanner.security.JWTAuthenticationEntryPoint;
import cookplanner.security.JWTTokenProvider;

@WebMvcTest(MeasureUnitController.class)
class MeasureUnitControllerTest {
	
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
	
	private MeasureUnit getTestMeasureUnit(Long id, String name, String pluralName) {
		MeasureUnit measureUnit = new MeasureUnit();
		measureUnit.setId(id);
		measureUnit.setName(name);
		measureUnit.setPluralName(pluralName);
		return measureUnit;
	}
}
