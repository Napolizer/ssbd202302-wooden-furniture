package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;

import java.io.File;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
public class AccountFacadeOperationsIT {
  @Inject
  private AccountFacadeOperations accountFacadeOperations;
  private Address address;
  private Person person;
  private Account account;
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
        .addAsResource(new File("src/test/resources/"), "");
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
    em.createQuery("DELETE FROM access_level").executeUpdate();
    em.createQuery("DELETE FROM Account").executeUpdate();
    em.createQuery("DELETE FROM Person").executeUpdate();
    em.createQuery("DELETE FROM Address").executeUpdate();
    utx.commit();
  }

  @Test
  public void properlyGetsAllAccounts() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {
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
  public void properlyAddsAccount() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    assertEquals(0, accountFacadeOperations.findAll().size());

    utx.begin();
    Account persistedAccount = accountFacadeOperations.create(account);
    utx.commit();

    List<Account> allAccounts = accountFacadeOperations.findAll();

    assertEquals(1, allAccounts.size());
    assertEquals(persistedAccount, allAccounts.get(0));
  }

  @Test
  public void failsToAddAccountWithoutLogin() {
    Account accountWithoutLogin = Account.builder()
        .password("password")
        .email("email")
        .person(person)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  public void failsToAddAccountWithoutPassword() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .email("email")
        .person(person)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  public void failsToAddAccountWithoutEmail() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .password("password")
        .person(person)
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  public void failsToAddAccountWithoutPerson() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .password("password")
        .email("email")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  public void failsToAddAccountWithoutLocale() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .password("password")
        .email("email")
        .person(person)
        .accountState(AccountState.ACTIVE)
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  public void failsToAddAccountWithoutAccountState() {
    Account accountWithoutLogin = Account.builder()
        .login("login")
        .password("password")
        .email("email")
        .person(person)
        .locale("pl")
        .build();

    assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
  }

  @Test
  public void failsToAddNewAccountWithAlreadyAssignedAddress() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
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
        .build();

    utx.begin();
    assertDoesNotThrow(() -> accountFacadeOperations.create(account));
    utx.commit();

    utx.begin();
    assertThrows(BaseWebApplicationException.class, () -> accountFacadeOperations.create(wrongAccountWithAlreadyAssignedAddress));
    utx.rollback();
  }

  @Test
  public void properlyGetsAccountById() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    Account persistedAccount = accountFacadeOperations.create(account);
    utx.commit();
    assertEquals(persistedAccount,
        accountFacadeOperations.find(persistedAccount.getId()).orElse(null));
  }

  @Test
  public void failsToGetAccountByIdWhenAccountDoesNotExist() {
    assertTrue(accountFacadeOperations.find(0L).isEmpty());
  }

  @Test
  public void properlyGetsAllAccountsByFirstName() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
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
        .build();

    utx.begin();
    assertDoesNotThrow(() -> accountFacadeOperations.create(account));
    assertDoesNotThrow(() -> accountFacadeOperations.create(account2));
    assertDoesNotThrow(() -> accountFacadeOperations.create(account3));

    String nameJohn = "John";
    String nameJan = "Jan";
    String nameKizo = "Kizo";

    List<Account> accountsWithNameJohn = accountFacadeOperations.findAllByFirstName(nameJohn);
    assertEquals(2, accountsWithNameJohn.size());
    assertEquals(nameJohn, accountsWithNameJohn.get(0).getPerson().getFirstName());
    assertEquals(nameJohn, accountsWithNameJohn.get(1).getPerson().getFirstName());

    List<Account> accountsWithNameJan = accountFacadeOperations.findAllByFirstName(nameJan);
    assertEquals(1, accountsWithNameJan.size());
    assertEquals(nameJan, accountsWithNameJan.get(0).getPerson().getFirstName());

    List<Account> accountsWithNameKizo = accountFacadeOperations.findAllByFirstName(nameKizo);
    assertEquals(0, accountsWithNameKizo.size());

    utx.commit();
  }

  @Test
  public void properlyGetsAllAccountsByLastName() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
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
        .build();

    utx.begin();
    assertDoesNotThrow(() -> accountFacadeOperations.create(account));
    assertDoesNotThrow(() -> accountFacadeOperations.create(account2));
    assertDoesNotThrow(() -> accountFacadeOperations.create(account3));

    String lastNameSmith = "Smith";
    String lastNameDzban = "Dzban";
    String lastNameKizo = "Kizo";

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

    utx.commit();
  }

  @Test
  public void properlyGetsAccountByLogin() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    Account persistedAccount = accountFacadeOperations.create(account);
    assertEquals(persistedAccount,
        accountFacadeOperations.findByLogin(persistedAccount.getLogin()).orElse(null));
    utx.commit();
  }

  @Test
  public void failsToGetAccountByLoginWhenAccountDoesNotExist() {
    assertThrows(EJBException.class, () -> accountFacadeOperations.findByLogin("login"));
  }

  @Test
  public void properlyGetsAccountsByAddressId() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    assertDoesNotThrow(() -> accountFacadeOperations.create(account));
    List<Account> accountsWithSameAddress =
        accountFacadeOperations.findAllByAddressId(address.getId());
    utx.commit();
    assertEquals(1, accountsWithSameAddress.size());
    assertEquals(address, accountsWithSameAddress.get(0).getPerson().getAddress());

  }

  @Test
  public void properlyGetsAccountByEmail() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    Account persistedAccount = accountFacadeOperations.create(account);
    assertEquals(persistedAccount,
        accountFacadeOperations.findByEmail(persistedAccount.getEmail()).orElse(null));
    utx.commit();
  }

  @Test
  public void failsToGetAccountByEmailWhenAccountDoesNotExist() {
    assertThrows(EJBException.class, () -> accountFacadeOperations.findByEmail("email"));
  }
}
