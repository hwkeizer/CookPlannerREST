//package cookplanner.controller;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import cookplanner.domain.Planning;
//import cookplanner.domain.Recipe;
//import cookplanner.repository.PlanningRepository;
//import cookplanner.security.CustomUserDetailsService;
//import cookplanner.security.JWTAuthenticationEntryPoint;
//import cookplanner.security.JWTTokenProvider;
//import cookplanner.service.PlanBoardService;
//
//@WebMvcTest(value = PlanningController.class)
//class PlanningControllerTest {
//	
//	ObjectMapper objectMapper = new ObjectMapper();
//
//	@Autowired
//	private MockMvc mockMvc;
//	
//	@MockBean PlanningRepository planningRepository;
//	@MockBean PlanBoardService planBoardService;
//	
//	// MockBeans required for securityContext
//	@MockBean CustomUserDetailsService cudService;
//	@MockBean JWTAuthenticationEntryPoint jwtEntryPoint;
//	@MockBean JWTTokenProvider jwtProvider;
//	
//	@Test
//	@WithMockUser
//	void testGetPlanningList() throws Exception {
//		// Prepare list with two plannings
//		List<Planning> planningList = new ArrayList<>();
//		planningList.add(getTestPlanning(1L, new Recipe("test recept")));
//		planningList.add(getTestPlanning(2L));
//		when(planningRepository.findAll()).thenReturn(planningList);
//		
//		// Execute & verify
//		mockMvc.perform(get("/planning/list")
//				.accept(MediaType.APPLICATION_JSON_UTF8))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andExpect(jsonPath("$.message").value("Planning succesvol opgehaald"))
//				.andExpect(jsonPath("$.result.[0].name").value("test recept"))
//				.andExpect(jsonPath("$.result.[1].name").value("- Geen recept gepland -"));
//	}
//	
//	@Test
//	@WithMockUser
//	void testCreatePlanning_HappyPath() throws Exception {
//		// Prepare
//		Planning planning = getTestPlanning(1L);
//		when(planningRepository.findById(planning.getId())).thenReturn(Optional.empty());
//		when(planningRepository.save(planning)).thenReturn(planning);
//		
//		// Execute & verify
//		mockMvc.perform(post("/planning/create")
//				.contentType(MediaType.APPLICATION_JSON_UTF8)
//				.content(objectMapper.writeValueAsString(planning)))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.message").value("Planning succesvol aangemaakt"))
//				.andExpect(jsonPath("$.result.name").value("- Geen recept gepland -"));
//	}
//	
//	@Test
//	@WithMockUser
//	void testCreatePlanning_AlreadyExists() throws Exception {
//		// Prepare
//		Planning planning = getTestPlanning(1L, new Recipe("test recept"));
//		when(planningRepository.findById(planning.getId())).thenReturn(Optional.of(planning));
//		
//		// Execute & verify
//		MvcResult result = mockMvc.perform(post("/planning/create")
//				.contentType(MediaType.APPLICATION_JSON_UTF8)
//				.content(objectMapper.writeValueAsString(planning)))
//				.andExpect(status().isConflict())
//				.andReturn();
//		assertEquals(result.getResponse().getErrorMessage(), "Planning bestaat al");
//	}
//	
//	@Test
//	@WithMockUser
//	void testDeletePlanning_HappyPath() throws Exception {
//		// Prepare
//		Planning planning =getTestPlanning(1L);
//		when(planningRepository.findById(planning.getId())).thenReturn(Optional.of(planning));
//		
//		// Execute & verify
//		mockMvc.perform(delete("/planning/delete/1")
//				.contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.message").value("Planning succesvol verwijderd"))
//				.andExpect(jsonPath("$.result").value("1"));
//		verify(planningRepository, times(1)).deleteById(planning.getId());
//		verify(planningRepository, times(1)).existsById(planning.getId());
//	}
//	
//	@Test
//	@WithMockUser
//	void testDeletePlanning_NotDeleted() throws Exception {
//		// Prepare
//		Planning planning =getTestPlanning(1L);
//		when(planningRepository.findById(planning.getId())).thenReturn(Optional.of(planning));
//		when(planningRepository.existsById(planning.getId())).thenReturn(true);
//		
//		// Execute & verify
//		MvcResult result = mockMvc.perform(delete("/planning/delete/1")
//				.contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andExpect(status().isMethodNotAllowed())
//				.andReturn();
//		assertEquals(result.getResponse().getErrorMessage(), "Kon planning niet verwijderen");
//	}
//	
//	private Planning getTestPlanning(Long id) {
//		Planning planning = new Planning();
//		planning.setId(id);
//		return planning;
//	}
//	
//	private Planning getTestPlanning(Long id, Recipe recipe) {
//		Planning planning = new Planning();
//		planning.setId(id);
//		planning.setRecipe(recipe);
//		return planning;
//	}
//}
