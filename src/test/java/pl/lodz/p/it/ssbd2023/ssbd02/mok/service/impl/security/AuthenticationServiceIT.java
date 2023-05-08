package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.transaction.*;

import java.io.File;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotVerifiedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountArchiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountBlockedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountIsInactiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.InvalidCredentialsException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@ExtendWith(ArquillianExtension.class)
public class AuthenticationServiceIT {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
  @Resource
  private UserTransaction utx;
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private TokenService tokenService;
  @Inject
  private AccountFacadeOperations accountFacade;

  private Account account;
  private String password = "test123";

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class)
        .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
        .addPackages(true, "org.postgresql")
        .addPackages(true, "org.hamcrest")
        .addPackages(true, "io.jsonwebtoken")
        .addPackages(true, "javax.xml.bind")
        .addPackages(true, "at.favre")
        .addAsResource(new File("src/test/resources/"), "");
  }

  @BeforeEach
  public void setUp() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    account = Account.builder()
        .login("test")
        .password(CryptHashUtils.hashPassword(password))
        .email("test@gmail.com")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .failedLoginCounter(0)
        .person(Person.builder()
            .firstName("John")
            .lastName("Doe")
            .address(Address.builder()
                .country("Poland")
                .city("Lodz")
                .street("Koszykowa")
                .postalCode("90-000")
                .streetNumber(12)
                .build())
            .build())
        .build();
    accountFacade.create(account);
    Client client = Client.builder()
            .company(null)
            .account(account)
            .build();
    account.setAccessLevels(List.of(client));
    accountFacade.update(account);
    utx.commit();
  }

  @AfterEach
  public void tearDown() throws Exception {
    utx.begin();
    em.createQuery("DELETE FROM access_level").executeUpdate();
    em.createQuery("DELETE FROM Account").executeUpdate();
    em.createQuery("DELETE FROM Person").executeUpdate();
    em.createQuery("DELETE FROM Address").executeUpdate();
    utx.commit();
  }

  @Test
  void shouldProperlyLoginTest() throws AuthenticationException, SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
      String token = authenticationService.login(account.getLogin(), password, "127.0.0.1");
      assertNotNull(token);
      utx.begin();
      List<AccessLevel> accessLevels = tokenService.getTokenClaims(token).getAccessLevels();
      utx.commit();
      assertThat(accessLevels, is(not(empty())));
      assertThat(accessLevels.size(), is(equalTo(1)));
      assertThat(accessLevels.get(0), is(instanceOf(Client.class)));
  }

  @Test
  void shouldFailToLoginIfAccountWithGivenLoginDoesNotExist() {
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login("nonexistent", "nonexistent", "127.0.0.1");
    });
  }

  @Test
  void shouldFailToLoginIfAccountWithGivenLoginIsInactive() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {
    account.setAccountState(AccountState.INACTIVE);
    utx.begin();
    accountFacade.update(account);
    utx.commit();
    assertThrows(AccountIsInactiveException.class, () -> authenticationService.login(account.getLogin(), account.getPassword(), "127.0.0.1"));
  }

  @Test
  void shouldFailToLoginIfAccountWithGivenLoginIsNotVerified() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {
    account.setAccountState(AccountState.NOT_VERIFIED);
    utx.begin();
    accountFacade.update(account);
    utx.commit();
    assertThrows(AccountNotVerifiedException.class, () -> authenticationService.login(account.getLogin(), account.getPassword(), "127.0.0.1"));
  }

  @Test
  void shouldFailToLoginIfAccountWithGivenLoginIsBlocked() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {
    account.setAccountState(AccountState.BLOCKED);
    utx.begin();
    accountFacade.update(account);
    utx.commit();
    assertThrows(AccountBlockedException.class, () -> authenticationService.login(account.getLogin(), account.getPassword(), "127.0.0.1"));
  }

  @Test
  void shouldFailToLoginIfAccountWithGivenLoginIsArchived() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {
    account.setArchive(true);
    utx.begin();
    accountFacade.update(account);
    utx.commit();
    assertThrows(AccountArchiveException.class, () -> authenticationService.login(account.getLogin(), account.getPassword(), "127.0.0.1"));
  }

  @Test
  void shouldFailToLoginIfPasswordIsInvalid() {
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login(account.getLogin(), "invalid", "127.0.0.1");
    });
  }

  @Test
  void shouldFailToLoginIfLoginIsNull() {
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login(null, "invalid", "127.0.0.1");
    });
  }

  @Test
  void shouldFailToLoginIfPasswordIsNull() {
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login(account.getLogin(), null, "127.0.0.1");
    });
  }

  @Test
  void shouldCorrectlyIncrementCounter() throws Exception {
    assertEquals(0, account.getFailedLoginCounter());
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login(account.getLogin(), "wrongOne", "127.0.0.1");
    });
    utx.begin();
    Account refreshedAccount = accountFacade.findById(account.getId())
        .orElseThrow();
    utx.commit();
    assertEquals(1, refreshedAccount.getFailedLoginCounter());
  }

//    @Test
//    public void shouldBlockAccountAfterThreeFailureAttempts() {
//        assertEquals(0, person.getAccount().getFailedLoginCounter());
//        assertEquals(AccountState.ACTIVE, person.getAccount().getAccountState());
//
//        for (int i = 0; i < 3; i++) {
//            assertThrows(InvalidCredentialsException.class, () -> {
//                authenticationService.login(person.getAccount().getLogin(), "wrongOne");
//            });
//        }
//
//        Person refreshedPerson = personFacadeOperations.find(person.getId()).orElseThrow();
//        assertEquals(0, refreshedPerson.getAccount().getFailedLoginCounter());
//        assertEquals(AccountState.BLOCKED, refreshedPerson.getAccount().getAccountState());
//    }
}
