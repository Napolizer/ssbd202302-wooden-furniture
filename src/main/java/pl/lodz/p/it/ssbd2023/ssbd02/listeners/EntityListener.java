package pl.lodz.p.it.ssbd2023.ssbd02.listeners;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

public class EntityListener {
  @PrePersist
  public void prePersist(AbstractEntity entity) {
    entity.setCreatedAt(LocalDateTime.now());

    String login = CDI.current().select(Principal.class).get().getName();
    AccountFacadeOperations accountFacade = CDI.current().select(AccountFacadeOperations.class).get();
    if (login.equals("ANONYMOUS")) {
      entity.setCreatedBy(null);
      return;
    }
    Optional<Account> accountOptional = accountFacade.findByLogin(login);
    if (accountOptional.isPresent()) {
      entity.setCreatedBy(accountOptional.get());
    } else {
      entity.setCreatedBy(null);
    }
  }

  @PreUpdate
  public void preUpdate(AbstractEntity entity) {
    entity.setUpdatedAt(LocalDateTime.now());

    String login = CDI.current().select(Principal.class).get().getName();
    AccountFacadeOperations accountFacade = CDI.current().select(AccountFacadeOperations.class).get();
    if (login.equals("ANONYMOUS")) {
      entity.setUpdatedBy(null);
      return;
    }
    Optional<Account> accountOptional = accountFacade.findByLogin(login);
    if (accountOptional.isPresent()) {
      entity.setUpdatedBy(accountOptional.get());
    } else {
      entity.setUpdatedBy(null);
    }
  }
}
