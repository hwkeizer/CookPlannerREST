package cookplanner.controller;

import static org.junit.Assert.assertEquals;
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
import cookplanner.repository.AccountRepository;
import cookplanner.security.CustomUserDetailsService;
import cookplanner.security.JWTAuthenticationEntryPoint;
import cookplanner.security.JWTTokenProvider;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

	ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	private AccountRepository accountRepository;	
	
	// MockBeans required for securityContext
	@MockBean CustomUserDetailsService cudService;
	@MockBean JWTAuthenticationEntryPoint jwtEntryPoint;
	@MockBean JWTTokenProvider jwtProvider;
		
	@Test
	@WithMockUser(roles = "ADMIN")
	void testGetAccountList_HappyPath() throws Exception {
		// Prepare
		List<Account> accountList = new ArrayList<>();
		accountList.add(getTestAccount(1L, "username_1", "password"));
		accountList.add(getTestAccount(2L, "username_2", "password"));
		when(accountRepository.findAll()).thenReturn(accountList);
		
		// Execute & verify
		mockMvc.perform(get("/account/list")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Accountlijst succesvol opgehaald"))
				.andExpect(jsonPath("$.result.[0].username").value("username_1"))
				.andExpect(jsonPath("$.result.[1].username").value("username_2"));
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	void testGetAccountList_EmptyList() throws Exception {
		// Prepare
		List<Account> accountList = new ArrayList<>();
		when(accountRepository.findAll()).thenReturn(accountList);
		
		// Execute & verify
		MvcResult result = mockMvc.perform(get("/account/list")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Geen accounts gevonden");
	}

	@Test
	void testRegisterAccount_HappyPath() throws Exception {
		// Prepare
		Account account = getTestAccount(1L, "username_1", "password");
		when(accountRepository.findAccountByUsername(account.getUsername())).thenReturn(Optional.empty());
		when(accountRepository.save(account)).thenReturn(account);

		// Execute & verify
		mockMvc.perform(post("/account/register")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(account)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Account succesvol geregistreerd"))
				.andExpect(jsonPath("$.result").value("username_1"));
	}
	
	@Test
	void testRegisterAccount_UserAlreadyExists() throws Exception {
		// prepare
		Account account = getTestAccount(1L, "username_1", "password");
		when(accountRepository.findAccountByUsername(account.getUsername())).thenReturn(Optional.of(account));
		
		// Execute & verify
		MvcResult result = mockMvc.perform(post("/account/register")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(account)))
				.andExpect(status().isConflict())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Gebruikersnaam bestaat al");
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	void testUpdateAccount_HappyPath() throws Exception {
		// Prepare
		Account account = getTestAccount(1L, "username_1", "password");
		Account accountResult = getTestAccount(1L, "username_2", "password");
		when(accountRepository.findById(accountResult.getId())).thenReturn(Optional.of(account));		
		when(accountRepository.save(accountResult)).thenReturn(accountResult);
		
		// Execute & verify
		mockMvc.perform(put("/account/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(accountResult)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Account succesvol gewijzigd"))
				.andExpect(jsonPath("$.result.username").value("username_2"));		
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	void testUpdateAccount_NotFound() throws Exception {
		// Prepare
		Account account = getTestAccount(1L, "username_1", "password");
		when(accountRepository.findById(account.getId())).thenReturn(Optional.empty());
		
		MvcResult result = mockMvc.perform(put("/account/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(account)))
				.andExpect(status().isNotFound())
				.andReturn();
		assertEquals(result.getResponse().getErrorMessage(), "Account niet gevonden");
		verify(accountRepository, times(1)).findById(account.getId());
		verify(accountRepository, times(0)).save(account);
		
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteAccount_HappyPath() throws Exception {
		// Prepare
		Account account = getTestAccount(1L, "username_1", "password");
		
		// Execute & verify
		mockMvc.perform(delete("/account/delete/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Account succesvol verwijderd"))
				.andExpect(jsonPath("$.result").value("1"));
		verify(accountRepository, times(1)).deleteById(account.getId());
		verify(accountRepository, times(1)).findById(account.getId());		
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteAccount_NotDeleted() throws Exception {
		// Prepare
		Account account = getTestAccount(1L, "username_1", "password");
		when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
		
		// Execute && verify
		MvcResult result = mockMvc.perform(delete("/account/delete/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isMethodNotAllowed())
				.andReturn();
		
		assertEquals(result.getResponse().getErrorMessage(), "Kon account niet verwijderen");
		verify(accountRepository, times(1)).deleteById(account.getId());
		verify(accountRepository, times(1)).findById(account.getId());
	}
	
	private Account getTestAccount(Long id, String username, String password) {
		Account account = new Account();
		account.setId(id);
		account.setUsername(username);
		account.setPassword(password);
		return account;
	}

}
