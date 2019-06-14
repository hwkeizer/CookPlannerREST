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

import cookplanner.domain.IngredientName;
import cookplanner.repository.IngredientNameRepository;
import cookplanner.security.CustomUserDetailsService;
import cookplanner.security.JWTAuthenticationEntryPoint;
import cookplanner.security.JWTTokenProvider;

@WebMvcTest(IngredientNameController.class)
class IngredientNameControllerTest {

	ObjectMapper objectMapper = new ObjectMapper();
	
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
	
	@Test
	@WithMockUser
	void testCreateIngredientName_HappyPath() throws Exception {
		// Prepare
		IngredientName ingredientName = getTestIngredientName(1L, "aardappel", "aardappels");
		when(ingredientNameRepository.findByName(ingredientName.getName())).thenReturn(Optional.empty());
		when(ingredientNameRepository.save(ingredientName)).thenReturn(ingredientName);
		
		// Execute & verify
		mockMvc.perform(post("/ingredient-name/create")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(ingredientName)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Ingredientnaam succesvol aangemaakt"))
				.andExpect(jsonPath("$.result.name").value("aardappel"));
	}
	
	@Test
	@WithMockUser
	void testCreateIngredientName_AlreadyExists() throws Exception {
		// Prepare
		IngredientName ingredientName = getTestIngredientName(1L, "aardappel", "aardappels");
		when(ingredientNameRepository.findByName(ingredientName.getName())).thenReturn(Optional.of(ingredientName));
		
		// Execute & verify
		MvcResult result = mockMvc.perform(post("/ingredient-name/create")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(ingredientName)))
				.andExpect(status().isConflict())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Ingredientnaam bestaat al");
	}
	
	@Test
	@WithMockUser
	void testUpdateIngredientName_HappyPath() throws Exception {
		// Prepare
		IngredientName ingredientName = getTestIngredientName(1L, "ardappel", "aardappels");
		IngredientName ingredientNameResult = getTestIngredientName(1L, "aardappel", "aardappels");
		when(ingredientNameRepository.findById(ingredientName.getId())).thenReturn(Optional.of(ingredientName));
		when(ingredientNameRepository.save(ingredientNameResult)).thenReturn(ingredientNameResult);
		
		// Execute & verify
		mockMvc.perform(put("/ingredient-name/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(ingredientNameResult)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Ingredientnaam succesvol gewijzigd"))
				.andExpect(jsonPath("$.result.name").value("aardappel"));	
	}
	
	@Test
	@WithMockUser
	void testUpdateIngredientName_NotFound() throws Exception {
		// Prepare
		IngredientName ingredientName = getTestIngredientName(1L, "aardappel", "aardappels");
		when(ingredientNameRepository.findById(ingredientName.getId())).thenReturn(Optional.empty());
		
		// Execute & verify
		MvcResult result = mockMvc.perform(put("/ingredient-name/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(ingredientName)))
				.andExpect(status().isNotFound())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Ingredientnaam niet gevonden");
		verify(ingredientNameRepository, times(1)).findById(ingredientName.getId());
		verify(ingredientNameRepository, times(0)).save(ingredientName);
	}
	
	@Test
	@WithMockUser
	void testDeleteIngredientName_HappyPath() throws Exception {
		// Prepare
		IngredientName ingredientName = getTestIngredientName(1L, "aardappel", "aardappels");
		
		// Execute & verify
		mockMvc.perform(delete("/ingredient-name/delete/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Ingredientnaam succesvol verwijderd"))
				.andExpect(jsonPath("$.result").value("1"));
		verify(ingredientNameRepository, times(1)).deleteById(ingredientName.getId());
		verify(ingredientNameRepository, times(1)).existsById(ingredientName.getId());
	}
	
	@Test
	@WithMockUser
	void testDeleteIngredientName_NotDeleted() throws Exception {
		// Prepare
		IngredientName ingredientName = getTestIngredientName(1L, "aardappel", "aardappels");
		when(ingredientNameRepository.existsById(ingredientName.getId())).thenReturn(true);
		
		// Execute & verify
		MvcResult result = mockMvc.perform(delete("/ingredient-name/delete/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isMethodNotAllowed())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Kon ingredientnaam niet verwijderen");
		verify(ingredientNameRepository, times(1)).deleteById(ingredientName.getId());
		verify(ingredientNameRepository, times(1)).existsById(ingredientName.getId());
	}
	
	private IngredientName getTestIngredientName(Long id, String name, String pluralName) {
		IngredientName ingredientName = new IngredientName();
		ingredientName.setId(id);
		ingredientName.setName(name);
		ingredientName.setPluralName(pluralName);
		return ingredientName;
	}

}
