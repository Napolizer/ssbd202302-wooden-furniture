package pl.lodz.p.it.ssbd2023.ssbd02.mok.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.io.File;
import java.util.List;
import java.util.Optional;
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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Administrator;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Employee;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelAlreadyAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelNotAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.IllegalAccountStateChangeException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;

@ExtendWith(ArquillianExtension.class)
public class AccountServiceIT {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
  @Resource
  private UserTransaction utx;
  @Inject
  private AccountFacadeOperations accountFacade;
  @Inject
  private AccountService accountService;

  private Account account;
  private Account accountToRegister;

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class)
        .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
        .addPackages(true, "org.postgresql")
        .addPackages(true, "org.hamcrest")
        .addPackages(true, "at.favre.lib")
        .addAsResource(new File("src/test/resources/"), "");
  }

  @BeforeEach
  public void setup() {
    account = accountFacade.create(
        Account.builder()
            .login("test")
            .password("test")
            .email("test@gmail.com")
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
            .locale("pl")
            .accountState(AccountState.ACTIVE)
            .build()
    );
    accountToRegister = Account.builder()
        .login("test123")
        .password("test123")
        .email("test123@gmail.com")
        .person(Person.builder()
            .firstName("Bob")
            .lastName("Joe")
            .address(Address.builder()
                .country("Poland")
                .city("Lodz")
                .street("Koszykowa")
                .postalCode("90-000")
                .streetNumber(15)
                .build())
            .build())
        .locale("pl")
        .build();
  }

  @AfterEach
  public void teardown() throws Exception {
    utx.begin();
    em.createQuery("DELETE FROM Person").executeUpdate();
    em.createQuery("DELETE FROM Address").executeUpdate();
    em.createQuery("DELETE FROM Account").executeUpdate();
    utx.commit();
  }

  @Test
  public void properlyGetsAccountByLogin() {
    Optional<Account> accountOptional = accountService.getAccountByLogin(account.getLogin());
    assertThat(accountOptional.isPresent(), is(equalTo(true)));
    assertThat(accountOptional.get(), is(equalTo(account)));
  }

  @Test
  public void failsToGetAccountByLoginWhenLoginIsNull() {
    Optional<Account> accountOptional = accountService.getAccountByLogin(null);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  public void failsToGetAccountByLoginWhenLoginIsEmpty() {
    Optional<Account> accountOptional = accountService.getAccountByLogin("");
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  public void failsToGetAccountByLoginWhenLoginDoesNotExist() {
    Optional<Account> accountOptional = accountService.getAccountByLogin("nonexistent");
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  public void properlyGetsAccountById() {
    Optional<Account> accountOptional = accountService.getAccountById(account.getId());
    assertThat(accountOptional.isPresent(), is(equalTo(true)));
    assertThat(accountOptional.get(), is(equalTo(account)));
  }

  @Test
  public void failsToGetAccountByIdWhenIdDoesNotExist() {
    Optional<Account> accountOptional = accountService.getAccountById(0L);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  public void failsToGetAccountByIdWhenIdIsNull() {
    assertNull(accountService.getAccountById(null).orElse(null));
  }

  @Test
  public void failsToGetAccountByIdWhenIdIsNegative() {
    Optional<Account> accountOptional = accountService.getAccountById(-1L);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  public void failsToGetAccountByIdWhenIdIsMaxLong() {
    Optional<Account> accountOptional = accountService.getAccountById(Long.MAX_VALUE);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  public void failsToGetAccountByIdWhenIdIsMinLong() {
    Optional<Account> accountOptional = accountService.getAccountById(Long.MIN_VALUE);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  public void properlyGetsAllAccounts() {
    List<Account> accounts = accountService.getAccountList();
    assertThat(accounts.isEmpty(), is(false));
    assertThat(accounts.size(), is(equalTo(1)));
    assertThat(accounts.get(0), is(equalTo(account)));
  }

  @Test
  public void properlyGetsEmptyAccountList() throws Exception {
    teardown();
    List<Account> accounts = accountService.getAccountList();
    assertThat(accounts.isEmpty(), is(true));
  }

  @Test
  public void properlyAddsNewAccessLevelToAccount()
      throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
    AccessLevel newAccessLevel = new Client();

    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), newAccessLevel);
    List<AccessLevel> accessLevels = accountFacade.find(account.getId()).orElseThrow().getAccessLevels();
    assertThat(accessLevels.size(), equalTo(1));
    assertTrue(accessLevels.get(0) instanceof Client);
  }

  @Test
  public void failsToAddAccessLevelWhenAccessLevelIsAdded()
      throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
    AccessLevel newAccessLevel = new Client();

    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), newAccessLevel);
    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
    assertThrows(AccessLevelAlreadyAssignedException.class,
        () -> accountService.addAccessLevelToAccount(account.getId(), newAccessLevel));
    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
  }

  @Test
  public void properlyRemovesAccessLevelFromAccount()
      throws AccessLevelAlreadyAssignedException, AccessLevelNotAssignedException, AccountNotFoundException {
    AccessLevel newAccessLevel = new Client();

    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), newAccessLevel);
    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
    accountService.removeAccessLevelFromAccount(account.getId(), newAccessLevel);
    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
  }

  @Test
  public void failsToRemoveAccessLevelWhenAccessLevelIsNotAdded()
      throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
    AccessLevel accessLevelClient = new Client();
    AccessLevel accessLevelAdmin = new Administrator();

    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), accessLevelClient);
    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
    assertThrows(AccessLevelNotAssignedException.class,
        () -> accountService.removeAccessLevelFromAccount(account.getId(), accessLevelAdmin));
    assertThat(accountFacade.find(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
  }

  @Test
  public void properlyEditsAccountInfo() throws Exception {
    Account editedAccount = Account.builder()
        .person(Person.builder()
            .firstName("Adam")
            .lastName("John")
            .address(Address.builder()
                .country("Poland")
                .city("Lodz")
                .street("Koszykowa")
                .postalCode("90-200")
                .streetNumber(24)
                .build())
            .build())
        .build();
    utx.begin();
    assertEquals(account.getPerson().getFirstName(),
        accountFacade.findByLogin("test").get().getPerson().getFirstName());
    assertEquals(account.getPerson().getLastName(), accountFacade.findByLogin("test").get().getPerson().getLastName());
    assertEquals(account.getPerson().getAddress().getStreetNumber(),
        accountFacade.findByLogin("test").get().getPerson().getAddress().getStreetNumber());
    accountService.editAccountInfo(account.getLogin(), editedAccount);
    utx.commit();

    utx.begin();
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getFirstName(), "Adam");
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getLastName(), "John");
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getAddress().getStreetNumber(), 24);
    utx.commit();
  }


  @Test
  public void properlyEditsAccountInfoAsAdmin() throws Exception {
    Account editedAccount = Account.builder()
        .email("test1@gmail.com")
        .person(Person.builder()
            .firstName("Jack")
            .lastName("Smith")
            .address(Address.builder()
                .country("Poland")
                .city("Warsaw")
                .street("Mickiewicza")
                .postalCode("92-100")
                .streetNumber(15)
                .build())
            .build())
        .build();
    utx.begin();
    assertEquals(account.getPerson().getFirstName(),
        accountFacade.findByLogin("test").get().getPerson().getFirstName());
    assertEquals(account.getPerson().getLastName(), accountFacade.findByLogin("test").get().getPerson().getLastName());
    assertEquals(account.getPerson().getAddress().getStreetNumber(),
        accountFacade.findByLogin("test").get().getPerson().getAddress().getStreetNumber());
    assertEquals(account.getEmail(), accountFacade.findByLogin("test").get().getEmail());
    accountService.editAccountInfoAsAdmin(account.getLogin(), editedAccount);
    utx.commit();

    utx.begin();
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getFirstName(), "Jack");
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getLastName(), "Smith");
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getAddress().getCountry(), "Poland");
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getAddress().getCity(), "Warsaw");
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getAddress().getStreet(), "Mickiewicza");
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getAddress().getPostalCode(), "92-100");
    assertEquals(accountFacade.findByLogin("test").get().getPerson().getAddress().getStreetNumber(), 15);
    assertEquals(accountFacade.findByLogin("test").get().getEmail(), "test1@gmail.com");
    utx.commit();
  }

  @Test
  public void properlyRegistersAccount() {
    assertDoesNotThrow(() -> accountService.registerAccount(accountToRegister));
    Account account = accountService.getAccountByLogin("test123").orElseThrow();
    assertEquals(2, accountService.getAccountList().size());
    assertEquals(AccountState.NOT_VERIFIED, account.getAccountState());
    assertEquals(false, account.getArchive());
    assertEquals(0, account.getFailedLoginCounter());
  }

  @Test
  public void properlyCreatesAccount() {
    accountToRegister.setAccountState(AccountState.ACTIVE);
    accountToRegister.setAccessLevels(List.of(new Client(), new Employee()));
    assertDoesNotThrow(() -> accountService.createAccount(accountToRegister));
    Account account = accountService.getAccountByLogin("test123").orElseThrow();
    assertEquals(2, accountService.getAccountList().size());
    assertEquals(AccountState.ACTIVE, account.getAccountState());
    assertEquals(2, account.getAccessLevels().size());
    assertEquals(false, account.getArchive());
    assertEquals(0, account.getFailedLoginCounter());
  }

  @Test
  public void failsToRegisterAccountWithSameLogin() {
    accountToRegister.setLogin(account.getLogin());
    assertThrows(Exception.class, () -> accountService.registerAccount(accountToRegister));
    assertEquals(1, accountService.getAccountList().size());
  }

  @Test
  public void properlyChangesPassword() {
    String newPassword = "newPassword";
    assertEquals("test", accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
    assertDoesNotThrow(() -> accountService.changePassword(account.getLogin(), newPassword));
    assertEquals(newPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
  }

  @Test
  public void failsToChangePasswordWhenGivenOldPassword() throws AccountNotFoundException {
    String oldPassword = account.getPassword();
    assertEquals(oldPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
    accountService.changePassword(account.getLogin(), oldPassword);
    assertEquals(oldPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
  }

  @Test
  public void properlyChangesPasswordAsAdmin() {
    String newPassword = "newPassword";
    assertEquals("test", accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
    assertDoesNotThrow(() -> accountService.changePasswordAsAdmin(account.getLogin(), newPassword));
    assertEquals(newPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
  }

  @Test
  public void failsToChangePasswordAsAdminWhenGivenOldPassword() throws AccountNotFoundException {
    String oldPassword = account.getPassword();
    assertEquals(oldPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
    accountService.changePasswordAsAdmin(account.getLogin(), oldPassword);
    assertEquals(oldPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
  }

  @Test
  public void properlyBlocksActiveAccount() {
    assertEquals(AccountState.ACTIVE, account.getAccountState());
    assertDoesNotThrow(() -> accountService.blockAccount(account.getId()));
    assertEquals(AccountState.BLOCKED, account.getAccountState());
  }

  @Test
  public void blockAlreadyBlockedAccountShouldThrowException() {
    accountToRegister.setAccountState(AccountState.BLOCKED);
    Account persisted = accountFacade.create(accountToRegister);

    assertThrows(IllegalAccountStateChangeException.class,
        () -> accountService.blockAccount(persisted.getId()));
  }

  @Test
  public void blockInactiveAccountShouldThrowException() {
    accountToRegister.setAccountState(AccountState.INACTIVE);
    Account persisted = accountFacade.create(accountToRegister);

    assertThrows(IllegalAccountStateChangeException.class,
        () -> accountService.blockAccount(persisted.getId()));
  }

  @Test
  public void blockNotVerifiedAccountShouldThrowException() {
    accountToRegister.setAccountState(AccountState.NOT_VERIFIED);
    Account persisted = accountFacade.create(accountToRegister);

    assertThrows(IllegalAccountStateChangeException.class,
        () -> accountService.blockAccount(persisted.getId()));
  }

  @Test
  public void blockNonExistingUserShouldThrowException() {
    assertThrows(AccountNotFoundException.class,
        () -> accountService.blockAccount(Long.MAX_VALUE));
  }


  @Test
  public void properlyActivatesBlockedAccount() {
    accountToRegister.setAccountState(AccountState.BLOCKED);
    Account account = accountFacade.create(accountToRegister);
    assertEquals(AccountState.BLOCKED, account.getAccountState());
    assertDoesNotThrow(() -> accountService.activateAccount(account.getId()));
    Account changed = accountService.getAccountByLogin(accountToRegister.getLogin()).orElseThrow();
    assertEquals(AccountState.ACTIVE, changed.getAccountState());
  }

  @Test
  public void properlyActivatesNotVerifiedAccount() {
    accountToRegister.setAccountState(AccountState.NOT_VERIFIED);
    Account account = accountFacade.create(accountToRegister);
    assertEquals(AccountState.NOT_VERIFIED, account.getAccountState());
    assertDoesNotThrow(() -> accountService.activateAccount(account.getId()));
    Account changed = accountService.getAccountByLogin(accountToRegister.getLogin()).orElseThrow();
    assertEquals(AccountState.ACTIVE, changed.getAccountState());
  }

  @Test
  public void activateAlreadyActivatedAccountShouldThrowException() {
    accountToRegister.setAccountState(AccountState.ACTIVE);
    Account account = accountFacade.create(accountToRegister);
    assertThrows(IllegalAccountStateChangeException.class,
        () -> accountService.activateAccount(account.getId()));
  }

  @Test
  public void activateInactiveAccountShouldThrowException() {
    accountToRegister.setAccountState(AccountState.INACTIVE);
    Account account = accountFacade.create(accountToRegister);
    assertThrows(IllegalAccountStateChangeException.class,
        () -> accountService.activateAccount(account.getId()));
  }

  @Test
  public void activateNonExistingAccountShouldThrowException() {
    assertThrows(AccountNotFoundException.class,
        () -> accountService.activateAccount(Long.MAX_VALUE));
  }

//    @Test
//    public void updateEmailWithSetNewEmail() {
//        personToRegister.getAccount().setAccountState(AccountState.ACTIVE);
//        personToRegister.getAccount().setNewEmail("newEmail@gmail.com");
//
//        Account account = personFacadeOperations.create(personToRegister).getAccount();
//        assertEquals("test123@gmail.com", account.getEmail());
//        assertEquals("newEmail@gmail.com", account.getNewEmail());
//
//        Account accountAfterUpdate = accountService.updateEmail(account.getId()).getAccount();
//        assertEquals("newEmail@gmail.com", accountAfterUpdate.getEmail());
//        assertNull(null, accountAfterUpdate.getNewEmail());
//    }
}
