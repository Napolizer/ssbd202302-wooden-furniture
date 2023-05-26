package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.AdminAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.SalesRepAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountSearchSettings;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Administrator;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Employee;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.PasswordHistory;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.SalesRep;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.SortBy;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TimeZone;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
public class AccountFacadeOperationsIT {
  @Inject
  private AccountFacadeOperations accountFacadeOperations;
  private Address address;
  private Person person;
  private Account account;
  @Inject
  private AdminAuth admin;
  @Inject
  private SalesRepAuth salesRep;
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
  @Resource
  private UserTransaction utx;

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class)
        .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
        .addPackages(true, "org.postgresql")
        .addPackages(true, "org.hamcrest")
        .addPackages(true, "io.jsonwebtoken")
        .addPackages(true, "org.apache")
        .addAsResource(new File("src/test/resources/"), "")
        .addAsWebInfResource(new File("src/test/resources/WEB-INF/glassfish-web.xml"), "glassfish-web.xml");
  }

  public static Account buildAccount(String firstName, String lastName) {
    return buildAccount(firstName, lastName, firstName, new Client());
  }

  public static Account buildAccount(String firstName, String lastName, String email) {
    return buildAccount(firstName, lastName, email, new Client());
  }

  public static Account buildAccount(String firstName, String lastName, String email, AccessLevel accessLevel) {
    return buildAccount(firstName, lastName, email, List.of(accessLevel));
  }

  public static Account buildAccount(String firstName, String lastName, String email, List<AccessLevel> accessLevelList) {
    Address address = Address.builder()
        .country("England")
        .city("London")
        .street("Fakestreet")
        .postalCode("40-200")
        .streetNumber(30)
        .build();

    Person person = Person.builder()
        .firstName(firstName)
        .lastName(lastName)
        .address(address)
        .build();
    Account account = Account.builder()
        .login(firstName)
        .password(firstName)
        .email(email)
        .person(person)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .accessLevels(accessLevelList)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();
    for (AccessLevel accessLevel : accessLevelList) {
      accessLevel.setAccount(account);
    }
    return account;
  }

  @BeforeEach
  public void setup() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {
    address = Address.builder()
        .country("England")
        .city("London")
        .street("Fakestreet")
        .postalCode("40-200")
        .streetNumber(30)
        .build();

    person = Person.builder()
        .firstName("John")
        .lastName("Smith")
        .address(address)
        .build();
    utx.begin();
    account = Account.builder()
              .login("login")
              .password("password")
              .email("email")
              .person(person)
              .locale("pl")
              .accountState(AccountState.ACTIVE)
              .accountType(AccountType.NORMAL)
              .timeZone(TimeZone.EUROPE_WARSAW)
              .build();
    em.createQuery("DELETE FROM access_level").executeUpdate();
    em.createQuery("DELETE FROM Account").executeUpdate();
    em.createQuery("DELETE FROM Person").executeUpdate();
    em.createQuery("DELETE FROM Address").executeUpdate();
    utx.commit();

  }

  @AfterEach
  public void teardown() throws Exception {
    utx.begin();
    em.createQuery("DELETE FROM access_level ").executeUpdate();
    em.createQuery("DELETE FROM PasswordHistory ").executeUpdate();
    em.createQuery("DELETE FROM Account").executeUpdate();
    em.createQuery("DELETE FROM Person").executeUpdate();
    em.createQuery("DELETE FROM Address").executeUpdate();
    utx.commit();
  }

  @Test
  void properlyGetsAllAccounts() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {
    Address address2 = Address.builder()
        .country("Poland")
        .city("Warsaw")
        .street("Street")
        .postalCode("98-100")
        .streetNumber(20)
        .build();

    Person person2 = Person.builder()
            .firstName("Jan")
            .lastName("Dzban")
            .address(address2)
            .build();

    Account account2 = Account.builder()
        .login("login2")
        .password("password")
        .email("email1")
        .person(person2)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    assertEquals(0, accountFacadeOperations.findAll().size());

    utx.begin();
    accountFacadeOperations.create(account);
    utx.commit();
    assertEquals(1, accountFacadeOperations.findAll().size());

    utx.begin();
    accountFacadeOperations.create(account2);
    utx.commit();
    assertEquals(2, accountFacadeOperations.findAll().size());
  }

  @Test
  void properlyAddsAccount() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    assertEquals(0, accountFacadeOperations.findAll().size());

    utx.begin();
    Account persistedAccount = accountFacadeOperations.create(account);
    utx.commit();

    List<Account> allAccounts = accountFacadeOperations.findAll();

    assertEquals(1, allAccounts.size());
    assertEquals(persistedAccount, allAccounts.get(0));
  }

  @Test
  void failsToAddAccountWithoutLogin() {
    Account accountWithoutLogin = Account.builder()
        .password("password")
        .email("email")
        .person(person)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  void failsToAddAccountWithoutPassword() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .email("email")
        .person(person)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  void failsToAddAccountWithoutEmail() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .password("password")
        .person(person)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  void failsToAddAccountWithoutPerson() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .password("password")
        .email("email")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  void failsToAddAccountWithoutLocale() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .password("password")
        .email("email")
        .person(person)
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  void failsToAddAccountWithoutAccountState() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .password("password")
        .email("email")
        .person(person)
        .locale("pl")
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  void failsToAddNewAccountWithAlreadyAssignedAddress() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    Person wrongPersonWithAlreadyAssignedAddress = Person.builder()
        .firstName("John")
        .lastName("Doe")
        .address(address)
        .build();

    Account wrongAccountWithAlreadyAssignedAddress = Account.builder()
        .login("login")
        .password("password")
        .email("email1")
        .person(wrongPersonWithAlreadyAssignedAddress)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    utx.begin();
    assertDoesNotThrow(() -> accountFacadeOperations.create(account));
    utx.commit();

    utx.begin();
    assertThrows(BaseWebApplicationException.class, () -> accountFacadeOperations.create(wrongAccountWithAlreadyAssignedAddress));
    utx.rollback();
  }

  @Test
  void properlyGetsAccountById() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    Account persistedAccount = accountFacadeOperations.create(account);
    utx.commit();
    assertEquals(persistedAccount,
        accountFacadeOperations.find(persistedAccount.getId()).orElse(null));
  }

  @Test
  void failsToGetAccountByIdWhenAccountDoesNotExist() {
    assertTrue(accountFacadeOperations.find(0L).isEmpty());
  }

  @Test
  void properlyGetsAllAccountsByFirstName() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    Address address2 = Address.builder()
        .country("Poland")
        .city("Warsaw")
        .street("Street")
        .postalCode("98-100")
        .streetNumber(20)
        .build();

    Person person2 = Person.builder()
            .firstName("John")
            .lastName("Smith")
            .address(address2)
            .build();

    Account account2 = Account.builder()
        .login("login2")
        .password("password")
        .email("email1")
        .person(person2)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    Address address3 = Address.builder()
        .country("Poland")
        .city("Warsaw")
        .street("Street")
        .postalCode("98-100")
        .streetNumber(10)
        .build();

    Person person3 = Person.builder()
        .firstName("Jan")
        .lastName("Dzban")
        .address(address3)
        .build();

    Account account3 = Account.builder()
        .login("login3")
        .password("password")
        .email("email2")
        .person(person3)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    utx.begin();
    assertDoesNotThrow(() -> accountFacadeOperations.create(account));
    assertDoesNotThrow(() -> accountFacadeOperations.create(account2));
    assertDoesNotThrow(() -> accountFacadeOperations.create(account3));

    String nameJohn = "John";
    String nameJan = "Jan";
    String nameKizo = "Kizo";

    admin.call(() -> {
      List<Account> accountsWithNameJohn = accountFacadeOperations.findAllByFirstName(nameJohn);
      assertEquals(2, accountsWithNameJohn.size());
      assertEquals(nameJohn, accountsWithNameJohn.get(0).getPerson().getFirstName());
      assertEquals(nameJohn, accountsWithNameJohn.get(1).getPerson().getFirstName());

      List<Account> accountsWithNameJan = accountFacadeOperations.findAllByFirstName(nameJan);
      assertEquals(1, accountsWithNameJan.size());
      assertEquals(nameJan, accountsWithNameJan.get(0).getPerson().getFirstName());

      List<Account> accountsWithNameKizo = accountFacadeOperations.findAllByFirstName(nameKizo);
      assertEquals(0, accountsWithNameKizo.size());
    });
    utx.commit();
  }

  @Test
  void properlyGetsAllAccountsByLastName() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    Address address2 = Address.builder()
        .country("Poland")
        .city("Warsaw")
        .street("Street")
        .postalCode("98-100")
        .streetNumber(20)
        .build();

    Person person2 = Person.builder()
            .firstName("John")
            .lastName("Smith")
            .address(address2)
            .build();

    Account account2 = Account.builder()
        .login("login2")
        .password("password")
        .email("email1")
        .person(person2)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    Address address3 = Address.builder()
        .country("Poland")
        .city("Warsaw")
        .street("Street")
        .postalCode("98-100")
        .streetNumber(10)
        .build();

    Person person3 = Person.builder()
        .firstName("Jan")
        .lastName("Dzban")
        .address(address3)
        .build();

    Account account3 = Account.builder()
        .login("login3")
        .password("password")
        .email("email2")
        .person(person3)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accountType(AccountType.NORMAL)
        .timeZone(TimeZone.EUROPE_WARSAW)
        .build();

    utx.begin();
    assertDoesNotThrow(() -> accountFacadeOperations.create(account));
    assertDoesNotThrow(() -> accountFacadeOperations.create(account2));
    assertDoesNotThrow(() -> accountFacadeOperations.create(account3));

    String lastNameSmith = "Smith";
    String lastNameDzban = "Dzban";
    String lastNameKizo = "Kizo";

    admin.call(() -> {
      List<Account> accountsWithLastNameSmith =
          accountFacadeOperations.findAllByLastName(lastNameSmith);
      assertEquals(2, accountsWithLastNameSmith.size());
      assertEquals(lastNameSmith, accountsWithLastNameSmith.get(0).getPerson().getLastName());
      assertEquals(lastNameSmith, accountsWithLastNameSmith.get(1).getPerson().getLastName());

      List<Account> accountsWithLastNameDzban =
          accountFacadeOperations.findAllByLastName(lastNameDzban);
      assertEquals(1, accountsWithLastNameDzban.size());
      assertEquals(lastNameDzban, accountsWithLastNameDzban.get(0).getPerson().getLastName());

      List<Account> accountsWithLastNameKizo =
          accountFacadeOperations.findAllByLastName(lastNameKizo);
      assertEquals(0, accountsWithLastNameKizo.size());
    });
    utx.commit();
  }

  @Test
  void properlyGetsAccountByLogin() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    Account persistedAccount = accountFacadeOperations.create(account);
    utx.commit();
    salesRep.call(() -> {
      assertEquals(persistedAccount,
          accountFacadeOperations.findByLogin(persistedAccount.getLogin()).orElse(null));
    });
  }

  @Test
  void failsToGetAccountByLoginWhenAccountDoesNotExist() {
    salesRep.call(() -> {
      assertTrue(accountFacadeOperations.findByLogin("login").isEmpty());
    });
  }

  @Test
  void properlyGetsAccountsByAddressId() throws Exception {
    utx.begin();
    assertDoesNotThrow(() -> accountFacadeOperations.create(account));

    admin.call(() -> {
      List<Account> accountsWithSameAddress =
          accountFacadeOperations.findAllByAddressId(address.getId());

      assertEquals(1, accountsWithSameAddress.size());
      assertEquals(address, accountsWithSameAddress.get(0).getPerson().getAddress());
    });
    utx.commit();

  }

  @Test
  void properlyGetsAccountByEmail() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    Account persistedAccount = accountFacadeOperations.create(account);
    utx.commit();
    admin.call(() -> {
      assertEquals(persistedAccount,
          accountFacadeOperations.findByEmail(persistedAccount.getEmail()).orElse(null));
    });
  }

  @Test
  void failsToGetAccountByEmailWhenAccountDoesNotExist() {
    admin.call(() -> {
      assertFalse(accountFacadeOperations.findByEmail("email").isPresent());
    });
  }

  @Test
  void properlyDeletesAccount() throws Exception {
    utx.begin();
    admin.call(() -> {
      assertEquals(0, accountFacadeOperations.findAll().size());
      accountFacadeOperations.create(account);
      assertEquals(1, accountFacadeOperations.findAll().size());
      accountFacadeOperations.delete(account);
      assertEquals(0, accountFacadeOperations.findAll().size());
    });
    utx.commit();
  }

  @Test
  void properlyAddsNewHashToPasswordHistory() throws Exception {
    utx.begin();
    admin.call(() -> {
      assertEquals(0, accountFacadeOperations.findAll().size());
      account.getPasswordHistory().add(PasswordHistory.builder().hash("password").build());
      accountFacadeOperations.create(account);
      assertEquals(1, accountFacadeOperations.findAll().size());
    });
    utx.commit();

    utx.begin();
    admin.call(() -> {
      Account created = accountFacadeOperations.findByLogin(account.getLogin()).orElseThrow();
      assertEquals(1, created.getPasswordHistory().size());
    });
    utx.commit();
  }

  @Test
  void properlyFindsByFullNameLike() throws Exception {
    utx.begin();
    admin.call(() -> {
      assertEquals(0, accountFacadeOperations.findAll().size());
      accountFacadeOperations.create(buildAccount("John", "Doe"));
      accountFacadeOperations.create(buildAccount("Johny", "Donovan"));
      accountFacadeOperations.create(buildAccount("Joe", "Davis"));

      assertEquals(3, accountFacadeOperations.findAll().size());
    });
    utx.commit();
    utx.begin();
    admin.call(() -> {
      assertEquals(1, accountFacadeOperations.findByFullNameLike("joHN DOE").size());
      assertEquals(1, accountFacadeOperations.findByFullNameLike("DOE").size());
      assertEquals(2, accountFacadeOperations.findByFullNameLike("JOHN").size());
      assertEquals(3, accountFacadeOperations.findByFullNameLike("jo").size());
      assertEquals(3, accountFacadeOperations.findByFullNameLike("JO").size());
      assertEquals(1, accountFacadeOperations.findByFullNameLike("john ").size());
      assertEquals(1, accountFacadeOperations.findByFullNameLike(" donovan").size());
    });
    utx.commit();
  }

  @Test
  void failsToFindByFullNameLike() throws Exception {
    utx.begin();
    admin.call(() -> {
      assertEquals(0, accountFacadeOperations.findAll().size());
      accountFacadeOperations.create(buildAccount("John", "Doe"));

      assertEquals(1, accountFacadeOperations.findAll().size());
    });
    utx.commit();
    utx.begin();
    admin.call(() -> {
      assertEquals(0, accountFacadeOperations.findByFullNameLike(" j").size());
      assertEquals(0, accountFacadeOperations.findByFullNameLike("john  ").size());
      assertEquals(0, accountFacadeOperations.findByFullNameLike("moe").size());
      assertEquals(0, accountFacadeOperations.findByFullNameLike("  doe").size());
      assertEquals(0, accountFacadeOperations.findByFullNameLike("johndoe").size());
    });
    utx.commit();
  }
}
