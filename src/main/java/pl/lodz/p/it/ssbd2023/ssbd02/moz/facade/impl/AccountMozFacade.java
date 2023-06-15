package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.AccountMozFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.AccountFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    AccountFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class AccountMozFacade extends AbstractFacade<Account> implements AccountMozFacadeOperations {

  @PersistenceContext(unitName = "ssbd02mozPU")
  private EntityManager em;

  public AccountMozFacade() {
    super(Account.class);
  }

  @Override
  @RolesAllowed(CLIENT)
  public Optional<Account> findByLogin(String login) {
    try {
      return Optional.of(em.createNamedQuery(Account.FIND_BY_LOGIN, Account.class)
              .setParameter("login", login)
              .getSingleResult());
    } catch (PersistenceException e) {
      return Optional.empty();
    }
  }

  @Override
  @PermitAll
  protected EntityManager getEntityManager() {
    return em;
  }
}
