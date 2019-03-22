package cookplanner.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
	void testRegister_HappyPath() throws Exception {
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
	void testRegister_UserAlreadyExists() throws Exception {
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
	
	private Account getTestAccount(Long id, String username, String password) {
		Account account = new Account();
		account.setId(id);
		account.setUsername(username);
		account.setPassword(password);
		return account;
	}

}
