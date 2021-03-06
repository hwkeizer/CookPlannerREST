package cookplanner.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
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
import cookplanner.domain.Account;
import cookplanner.exception.AccountDoesNotExistException;
import cookplanner.exception.AccountListEmptyException;
import cookplanner.exception.AccountNotDeletedException;
import cookplanner.exception.UsernameAlreadyExistsException;
import cookplanner.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/account")
public class AccountController implements IApiResponse {
	
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	

	public AccountController(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/list")
	public ApiResponse<List<Account>> getAccountList() throws AccountListEmptyException {
		List<Account> accountList = accountRepository.findAll();
		if (!accountList.isEmpty()) {			
			return createResponse(
					200, 
					"Accountlijst succesvol opgehaald", 
					accountList);
		}
		throw new AccountListEmptyException();
	}

	@PostMapping("/register")
	public ApiResponse<String> registerAccount(@RequestBody Account account) throws UsernameAlreadyExistsException {
		if (accountRepository.findAccountByUsername(account.getUsername()).isPresent()) {
			throw new UsernameAlreadyExistsException();
		}
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		account.setCreatedOn(LocalDateTime.now());
		account.setUpdatedOn(LocalDateTime.now());
		return createResponse(
				200, 
				"Account succesvol geregistreerd", 
				accountRepository.save(account).getUsername());
	}
	
	@PutMapping("/update")
	public ApiResponse<Account> updateAccount(@RequestBody Account account) throws AccountDoesNotExistException {
		if (!accountRepository.findById(account.getId()).isPresent()) {
			throw new AccountDoesNotExistException();
		}
		account.setUpdatedOn(LocalDateTime.now());
		Account accountResult = accountRepository.save(account);
		return createResponse(
				200, 
				"Account succesvol gewijzigd", 
				accountResult);
	}

	@DeleteMapping("/delete/{id}")
	public ApiResponse<String> deleteAccount(@PathVariable String id) throws AccountNotDeletedException {
		accountRepository.deleteById(Long.parseLong(id));
		if (accountRepository.findById(Long.parseLong(id)).isPresent()) {
			throw new AccountNotDeletedException();
		}
		return createResponse(
				200, 
				"Account succesvol verwijderd",
				id);
	}
}
