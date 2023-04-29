package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
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
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountArchiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountBlockedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountIsInactiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.InvalidCredentialsException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

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

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class)
        .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
        .addPackages(true, "org.postgresql")
        .addPackages(true, "org.hamcrest")
        .addPackages(true, "io.jsonwebtoken")
        .addPackages(true, "javax.xml.bind")
        .addAsResource(new File("src/test/resources/"), "");
  }

  @BeforeEach
  public void setUp() {
    account = Account.builder()
        .login("test")
        .password("test")
        .email("test@gmail.com")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accessLevels(List.of(new Client()))
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
  }

  @AfterEach
  public void tearDown() throws Exception {
    utx.begin();
    em.createQuery("DELETE FROM Person").executeUpdate();
    em.createQuery("DELETE FROM Address").executeUpdate();
    em.createQuery("DELETE FROM Account").executeUpdate();
    utx.commit();
  }

  @Test
  public void shouldProperlyLoginTest() {
    assertDoesNotThrow(() -> {
      String token = authenticationService.login(account.getLogin(), account.getPassword());
      assertNotNull(token);
      List<AccessLevel> accessLevels = tokenService.getTokenClaims(token).getAccessLevels();
      assertThat(accessLevels, is(not(empty())));
      assertThat(accessLevels.size(), is(equalTo(1)));
      assertThat(accessLevels.get(0), is(instanceOf(Client.class)));
    });
  }

  @Test
  public void shouldFailToLoginIfAccountWithGivenLoginDoesNotExist() {
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login("nonexistent", "nonexistent");
    });
  }

  @Test
  public void shouldFailToLoginIfAccountWithGivenLoginIsInactive() {
    account.setAccountState(AccountState.INACTIVE);
    accountFacade.update(account);
    assertThrows(AccountIsInactiveException.class, () -> {
      authenticationService.login(account.getLogin(), account.getPassword());
    });
  }

  @Test
  public void shouldFailToLoginIfAccountWithGivenLoginIsBlocked() {
    account.setAccountState(AccountState.BLOCKED);
    accountFacade.update(account);
    assertThrows(AccountBlockedException.class, () -> {
      authenticationService.login(account.getLogin(), account.getPassword());
    });
  }

  @Test
  public void shouldFailToLoginIfAccountWithGivenLoginIsArchived() {
    account.setArchive(true);
    accountFacade.update(account);
    assertThrows(AccountArchiveException.class, () -> {
      authenticationService.login(account.getLogin(), account.getPassword());
    });
  }

  @Test
  public void shouldFailToLoginIfPasswordIsInvalid() {
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login(account.getLogin(), "invalid");
    });
  }

  @Test
  public void shouldFailToLoginIfLoginIsNull() {
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login(null, "invalid");
    });
  }

  @Test
  public void shouldFailToLoginIfPasswordIsNull() {
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login(account.getLogin(), null);
    });
  }

  @Test
  public void shouldCorrectlyIncrementCounter() throws Exception {
    assertEquals(0, account.getFailedLoginCounter());
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticationService.login(account.getLogin(), "wrongOne");
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
