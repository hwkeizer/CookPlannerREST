package cookplanner.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import cookplanner.domain.Recipe;
import cookplanner.repository.RecipeRepository;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = RecipeController.class)
class RecipeControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	RecipeRepository recipeRepo;
	
	@Test
	void testGetRecipeList_HappyPath() throws Exception {
		// prepare list with two recipes
		List<Recipe> recipeList = new ArrayList<>();
		recipeList.add(new Recipe("recipe 1"));
		recipeList.add(new Recipe("recipe 2"));
		when(recipeRepo.findAll()).thenReturn(recipeList);
		
		// execute and verify
		mockMvc.perform(get("/recipe/list")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$[0].name").value("recipe 1"))
				.andExpect(jsonPath("$[1].name").value("recipe 2"));		
	}
	
	@Test
	void testGetRecipeList_EmptyList() throws Exception {
		// prepare empty list
		List<Recipe> recipeList = new ArrayList<>();
		when(recipeRepo.findAll()).thenReturn(recipeList);
		
		// execute & verify
		MvcResult result = mockMvc.perform(get("/recipe/list"))
				.andExpect(status().isNotFound())
				.andReturn();
		assertEquals("Geen recepten gevonden", result.getResponse().getErrorMessage());
	}

}
