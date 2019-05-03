package cookplanner.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cookplanner.domain.Account;
import cookplanner.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private AccountRepository accountRepository;

	public CustomUserDetailsService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<Account> optionalAccount = accountRepository.findAccountByUsername(username);
		if (!optionalAccount.isPresent()) {
			throw new UsernameNotFoundException(username);
		}
		return optionalAccount.get();
	}
}
