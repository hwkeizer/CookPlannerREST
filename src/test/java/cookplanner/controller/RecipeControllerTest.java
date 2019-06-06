package cookplanner.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import cookplanner.domain.Recipe;
import cookplanner.domain.RecipeType;
import cookplanner.repository.RecipeRepository;
import cookplanner.repository.TagRepository;
import cookplanner.security.CustomUserDetailsService;
import cookplanner.security.JWTAuthenticationEntryPoint;
import cookplanner.security.JWTTokenProvider;
import cookplanner.service.FileSystemService;
import cookplanner.service.FileSystemServiceImpl;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = RecipeController.class)
class RecipeControllerTest {

	ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean RecipeRepository recipeRepository;
	@MockBean FileSystemService fileSystemService;
	@MockBean TagRepository tagRepository;	
	
	// MockBeans required for securityContext
	@MockBean CustomUserDetailsService cudService;
	@MockBean JWTAuthenticationEntryPoint jwtEntryPoint;
	@MockBean JWTTokenProvider jwtProvider;
	
	@Test
	@WithMockUser
	void testGetRecipeList_HappyPath() throws Exception {
		// prepare list with two recipes
		List<Recipe> recipeList = new ArrayList<>();
		recipeList.add(new Recipe("recipe 1"));
		recipeList.add(new Recipe("recipe 2"));
		when(recipeRepository.findAll()).thenReturn(recipeList);
		
		// execute and verify
		mockMvc.perform(get("/recipe/list")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message").value("Receptenlijst succesvol opgehaald"))
				.andExpect(jsonPath("$.result.[0].name").value("recipe 1"))
				.andExpect(jsonPath("$.result.[1].name").value("recipe 2"));		
	}
	
	@Test
	@WithMockUser
	void testGetRecipeList_EmptyList() throws Exception {
		// prepare empty list
		List<Recipe> recipeList = new ArrayList<>();
		when(recipeRepository.findAll()).thenReturn(recipeList);
		
		// execute & verify
		MvcResult result = mockMvc.perform(get("/recipe/list"))
				.andExpect(status().isNotFound())
				.andReturn();
		assertEquals("Geen recepten gevonden", result.getResponse().getErrorMessage());
	}
	
	@Test
	void testUpdateRecipe_HappyPath() throws Exception {
		// Prepare
		Recipe recipe = getTestRecipe(1L, "testRecept1");
		Recipe recipeResult = getTestRecipe(1L, "testRecept1_updated");
		when(recipeRepository.findById(recipeResult.getId())).thenReturn(Optional.of(recipe));		
		when(recipeRepository.save(recipeResult)).thenReturn(recipeResult);
		
		// Execute & verify
		mockMvc.perform(put("/recipe/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(recipeResult)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Recept succesvol gewijzigd"))
				.andExpect(jsonPath("$.result.name").value("testRecept1_updated"));		
	}
	
	@Test
	void testUpdateRecipe_NotFound() throws Exception {
		// Prepare
		Recipe recipe = getTestRecipe(1L, "testRecipe1");
		when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.empty());
		
		// Execute & verify
		MvcResult result = mockMvc.perform(put("/recipe/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(recipe)))
				.andExpect(status().isNotFound())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Recept niet gevonden");
		verify(recipeRepository, times(1)).findById(recipe.getId());
		verify(recipeRepository, times(0)).save(recipe);				
	}
	
	@Test
	void testGetRecipeTypes() throws Exception {
		// Prepare
		List<String> recipeTypes = new ArrayList<>();
		for (RecipeType type : RecipeType.values()) {
			recipeTypes.add(type.name());
		}

		// Execute & verify
		mockMvc.perform(get("/recipe/types")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Recept types succesvol opgehaald"))
				.andExpect(jsonPath("$.result").value(recipeTypes));			
	}
	
	@Test
	@WithMockUser
	void testDeleteRecipe_HappyPath() throws Exception {
		// Prepare
		Recipe recipe = getTestRecipe(1L, "testRecipe1");
		
		// Execute & verify
		mockMvc.perform(delete("/recipe/delete/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Recept succesvol verwijderd"))
				.andExpect(jsonPath("$.result").value("1"));
		verify(recipeRepository, times(1)).deleteById(recipe.getId());
		verify(recipeRepository, times(1)).findById(recipe.getId());
	}
	
	@Test
	@WithMockUser
	void testDeleteRecipe_NotDeleted() throws Exception {
		// Prepare
		Recipe recipe = getTestRecipe(1L, "testRecipe1");
		when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));
		
		// Execute & verify
		MvcResult result = mockMvc.perform(delete("/recipe/delete/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isMethodNotAllowed())
				.andReturn();
		
		assertEquals(result.getResponse().getErrorMessage(), "Kon recept niet verwijderen");
		verify(recipeRepository, times(1)).deleteById(recipe.getId());
		verify(recipeRepository, times(1)).findById(recipe.getId());
	}
	
	
	private Recipe getTestRecipe(Long id, String name) {
		Recipe recipe = new Recipe();
		recipe.setId(id);
		recipe.setName(name);
		return recipe;
	}

}
