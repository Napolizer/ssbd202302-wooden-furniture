package pl.lodz.p.it.ssbd2023.ssbd02.utils.listeners;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

public class OrderListener {

  private static final Long ORDER_MODIFICATION_BLOCK_TIME_IN_MINUTES = 133920L;

  @PrePersist
  public void prePersist(Order order) {
    order.setCreatedAt(LocalDateTime.now());
    order.setModificationBlockTime(order.getCreatedAt().plusMinutes(
        System.getenv("ORDER_MODIFICATION_BLOCK_TIME") == null
            ? ORDER_MODIFICATION_BLOCK_TIME_IN_MINUTES
            : Long.parseLong(System.getenv("ORDER_MODIFICATION_BLOCK_TIME"))
    ));

    String login = CDI.current().select(Principal.class).get().getName();
    AccountFacadeOperations accountFacade = CDI.current().select(AccountFacadeOperations.class).get();
    if (login.equals("ANONYMOUS")) {
      order.setCreatedBy(null);
      return;
    }
    Optional<Account> accountOptional = accountFacade.findByLogin(login);
    if (accountOptional.isPresent()) {
      order.setCreatedBy(accountOptional.get());
    } else {
      order.setCreatedBy(null);
    }

  }

  @PreUpdate
  public void preUpdate(Order order) {
    if (LocalDateTime.now().isAfter(order.getModificationBlockTime())) {
      throw ApplicationExceptionFactory.createModificationTimeExpiredException();
    }
    order.setUpdatedAt(LocalDateTime.now());

    String login = CDI.current().select(Principal.class).get().getName();
    AccountFacadeOperations accountFacade = CDI.current().select(AccountFacadeOperations.class).get();
    if (login.equals("ANONYMOUS")) {
      order.setUpdatedBy(null);
      return;
    }
    Optional<Account> accountOptional = accountFacade.findByLogin(login);
    if (accountOptional.isPresent()) {
      order.setUpdatedBy(accountOptional.get());
    } else {
      order.setUpdatedBy(null);
    }
  }
}
