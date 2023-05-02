package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.impl;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.AccountFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    AccountFacadeExceptionsInterceptor.class
})
public class AccountFacade extends AbstractFacade<Account> implements AccountFacadeOperations {
//  @Inject
//  AddressFacadeOperations addressFacadeOperations;

  @PersistenceContext(unitName = "ssbd02mokPU")
  private EntityManager em;

  public AccountFacade() {
    super(Account.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  public Account create(Account entity) {
    Account account = super.create(entity);
    em.flush();
    return account;
  }

  @Override
  public Account update(Account entity) {
    Account account = super.update(entity);
    em.flush();
    return account;
  }

  @Override
  public List<Account> findAllByFirstName(String firstName) {
    return em.createNamedQuery(Account.FIND_ALL_BY_FIRST_NAME, Account.class)
        .setParameter("firstName", firstName)
        .getResultList();
  }

  @Override
  public List<Account> findAllByLastName(String lastName) {
    return em.createNamedQuery(Account.FIND_ALL_BY_LAST_NAME, Account.class)
        .setParameter("lastName", lastName)
        .getResultList();
  }

  @Override
  public List<Account> findAllByAddressId(Long addressId) {
    return em.createNamedQuery(Account.FIND_ALL_BY_ADDRESS_ID, Account.class)
        .setParameter("addressId", addressId)
        .getResultList();
  }

  @Override
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
  public Optional<Account> findByEmail(String email) {
    try {
      return Optional.of(em.createNamedQuery(Account.FIND_BY_EMAIL, Account.class)
          .setParameter("email", email)
          .getSingleResult());
    } catch (PersistenceException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Account> findById(Long accountId) {
    try {
      return Optional.of(em.createNamedQuery(Account.FIND_BY_ACCOUNT_ID, Account.class)
          .setParameter("id", accountId)
          .getSingleResult());
    } catch (PersistenceException e) {
      return Optional.empty();
    }
  }

  @Override
  public void delete(Account account) {
    em.remove(em.merge(account));
  }


}
