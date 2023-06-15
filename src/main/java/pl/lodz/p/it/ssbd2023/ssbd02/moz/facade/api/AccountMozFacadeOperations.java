package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import jakarta.ejb.Local;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;

@Local
public interface AccountMozFacadeOperations {
  Optional<Account> findByLogin(String login);
}
