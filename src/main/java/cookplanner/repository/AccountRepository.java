package cookplanner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cookplanner.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

	public Optional<Account> findAccountByUsername(String email);
}
