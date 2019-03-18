package cookplanner.controller;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cookplanner.api.ApiResponse;
import cookplanner.api.JWTAuthenticationResponse;
import cookplanner.domain.Account;
import cookplanner.repository.AccountRepository;
import cookplanner.security.JWTTokenProvider;

@RestController
@CrossOrigin(origins="http://localhost:4200")
public class LoginController implements IApiResponse {
	
	private final AuthenticationManager authenticationManager;
	private final AccountRepository accountRepository;
	JWTTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;


	public LoginController(AuthenticationManager authenticationManager, AccountRepository accountRepository,
			JWTTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.accountRepository = accountRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public ApiResponse<JWTAuthenticationResponse> authenticateAccount(@RequestBody Account account) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtTokenProvider.generateToken(authentication);
		Account authenticatedAccount = accountRepository.findAccountByUsername(account.getUsername()).get();
		
		JWTAuthenticationResponse jwtResponse = new JWTAuthenticationResponse(
				token,
				authenticatedAccount.getUsername(),
				authenticatedAccount.getRole());
		authenticatedAccount.setLastLogin(LocalDateTime.now());
		accountRepository.save(authenticatedAccount);
		return createResponse(200, "Authenticatie succesvol", jwtResponse);
	}
	
	@PostMapping("/account/register")
	public ApiResponse<String> register(@RequestBody Account account) {
		
		if (accountRepository.findAccountByUsername(account.getUsername()).isPresent()) {
			// TODO create custom exception for this situation
			return null;
		}
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		account.setCreatedOn(LocalDateTime.now());
		account.setUpdatedOn(LocalDateTime.now());
		accountRepository.save(account);
		return createResponse(200, "Account succesvol geregistreerd", account.getUsername());
	}
}
