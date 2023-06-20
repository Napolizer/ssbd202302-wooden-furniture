package pl.lodz.p.it.ssbd2023.ssbd02.utils.listeners;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

public class RateListener {
  @PrePersist
  public void prePersist(Rate rate) {
    rate.setCreatedAt(LocalDateTime.now());
    rate.setModificationBlockTime(rate.getCreatedAt().plusMinutes(
        System.getenv("RATE_MODIFICATION_BLOCK_TIME") == null ?
            44640 : Long.parseLong(System.getenv("RATE_MODIFICATION_BLOCK_TIME"))
    ));

    String login = CDI.current().select(Principal.class).get().getName();
    AccountFacadeOperations accountFacade = CDI.current().select(AccountFacadeOperations.class).get();
    if (login.equals("ANONYMOUS")) {
      rate.setCreatedBy(null);
      return;
    }
    Optional<Account> accountOptional = accountFacade.findByLogin(login);
    if (accountOptional.isPresent()) {
      rate.setCreatedBy(accountOptional.get());
    } else {
      rate.setCreatedBy(null);
    }

  }

  @PreUpdate
  public void preUpdate(Rate rate) {
    if (LocalDateTime.now().isAfter(rate.getModificationBlockTime())) {
      throw ApplicationExceptionFactory.createModificationTimeExpiredException();
    }
    rate.setUpdatedAt(LocalDateTime.now());

    String login = CDI.current().select(Principal.class).get().getName();
    AccountFacadeOperations accountFacade = CDI.current().select(AccountFacadeOperations.class).get();
    if (login.equals("ANONYMOUS")) {
      rate.setUpdatedBy(null);
      return;
    }
    Optional<Account> accountOptional = accountFacade.findByLogin(login);
    if (accountOptional.isPresent()) {
      rate.setUpdatedBy(accountOptional.get());
    } else {
      rate.setUpdatedBy(null);
    }
  }
}
