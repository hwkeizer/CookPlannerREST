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

import cookplanner.domain.IngredientName;
import cookplanner.repository.IngredientNameRepository;
import cookplanner.security.CustomUserDetailsService;
import cookplanner.security.JWTAuthenticationEntryPoint;
import cookplanner.security.JWTTokenProvider;

@WebMvcTest(IngredientNameController.class)
class IngredientNameControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	IngredientNameRepository ingredientNameRepository;
	
	// MockBeans required for securityContext
	@MockBean CustomUserDetailsService cudService;
	@MockBean JWTAuthenticationEntryPoint jwtEntryPoint;
	@MockBean JWTTokenProvider jwtProvider;
	
	@Test
	@WithMockUser
	void testGetIngredientNameList_HappyPath() throws Exception {
		// Prepare
		List<IngredientName> ingredientNameList = new ArrayList<>();
		ingredientNameList.add(getTestIngredientName(1L, "Tomaat", "Tomaten"));
		ingredientNameList.add(getTestIngredientName(2L, "Aardappel", "Aardappels"));
		when(ingredientNameRepository.findAll()).thenReturn(ingredientNameList);
		
		// Execute & verify
		mockMvc.perform(get("/ingredient-name/list")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Ingredientnaam lijst succesvol opgehaald"))
				.andExpect(jsonPath("$.result.[0].name").value("Tomaat"))
				.andExpect(jsonPath("$.result.[1].pluralName").value("Aardappels"));		
	}
	
	@Test
	@WithMockUser
	void testGetIngredientNameList_EmptyList() throws Exception {
		// Prepare
		List<IngredientName> ingredientNameList = new ArrayList<>();
		when(ingredientNameRepository.findAll()).thenReturn(ingredientNameList);
		
		// Execute & verify
		MvcResult result = mockMvc.perform(get("/ingredient-name/list")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Geen ingredientnamen gevonden");
	}
	
	private IngredientName getTestIngredientName(Long id, String name, String pluralName) {
		IngredientName ingredientName = new IngredientName();
		ingredientName.setId(id);
		ingredientName.setName(name);
		ingredientName.setPluralName(pluralName);
		return ingredientName;
	}

}
