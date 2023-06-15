package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;

import java.util.Optional;

@Local
public interface AccountMozFacadeOperations {
  Optional<Account> findByLogin(String login);
}
