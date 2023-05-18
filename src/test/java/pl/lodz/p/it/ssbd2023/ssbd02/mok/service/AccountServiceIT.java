package pl.lodz.p.it.ssbd2023.ssbd02.mok.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;

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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.AdminAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.ClientAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@ExtendWith(ArquillianExtension.class)
public class AccountServiceIT {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
  @Resource
  private UserTransaction utx;
  @Inject
  private AccountService accountService;
  @Inject
  private AdminAuth admin;
  @Inject
  private ClientAuth client;

  private Account account;
  private Account accountToRegister;

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class)
        .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
        .addPackages(true, "org.postgresql")
        .addPackages(true, "org.hamcrest")
        .addPackages(true, "at.favre.lib")
        .addPackages(true, "io.jsonwebtoken")
        .addPackages(true, "javax.xml.bind")
        .addAsResource(new File("src/test/resources/"), "")
        .addAsWebInfResource(new File("src/test/resources/WEB-INF/glassfish-web.xml"), "glassfish-web.xml");
  }

  @BeforeEach
  public void setup() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    accountService.createAccount(
            account = Account.builder()
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
    utx.begin();
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
  void properlyGetsAccountByLogin() {
    Optional<Account> accountOptional = accountService.getAccountByLogin(account.getLogin());
    assertThat(accountOptional.isPresent(), is(equalTo(true)));
    assertThat(accountOptional.get(), is(equalTo(account)));
  }

  @Test
  void failsToGetAccountByEmailWhenEmailIsNull() {
    Optional<Account> accountOptional = accountService.getAccountByEmail(null);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void failsToGetAccountByEmailWhenEmailIsEmpty() {
    Optional<Account> accountOptional = accountService.getAccountByEmail("");
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void failsToGetAccountByEmailWhenEmailDoesNotExist() {
    Optional<Account> accountOptional = accountService.getAccountByLogin("nonexistent@gmail.com");
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void properlyGetsAccountById() {
    Optional<Account> accountOptional = accountService.getAccountById(account.getId());
    assertThat(accountOptional.isPresent(), is(equalTo(true)));
    assertThat(accountOptional.get(), is(equalTo(account)));
  }

  @Test
  void failsToGetAccountByIdWhenIdDoesNotExist() {
    Optional<Account> accountOptional = accountService.getAccountById(0L);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void failsToGetAccountByIdWhenIdIsNull() {
    assertNull(accountService.getAccountById(null).orElse(null));
  }

  @Test
  void failsToGetAccountByIdWhenIdIsNegative() {
    Optional<Account> accountOptional = accountService.getAccountById(-1L);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void failsToGetAccountByIdWhenIdIsMaxLong() {
    Optional<Account> accountOptional = accountService.getAccountById(Long.MAX_VALUE);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void failsToGetAccountByIdWhenIdIsMinLong() {
    Optional<Account> accountOptional = accountService.getAccountById(Long.MIN_VALUE);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void properlyGetsAccountByEmail() {
    Optional<Account> accountOptional = accountService.getAccountByEmail(account.getEmail());
    assertThat(accountOptional.isPresent(), is(equalTo(true)));
    assertThat(accountOptional.get(), is(equalTo(account)));
  }

  @Test
  void failsToGetAccountByLoginWhenLoginIsNull() {
    Optional<Account> accountOptional = accountService.getAccountByLogin(null);
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void failsToGetAccountByLoginWhenLoginIsEmpty() {
    Optional<Account> accountOptional = accountService.getAccountByLogin("");
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void failsToGetAccountByLoginWhenLoginDoesNotExist() {
    Optional<Account> accountOptional = accountService.getAccountByLogin("nonexistent");
    assertThat(accountOptional.isPresent(), is(equalTo(false)));
  }

  @Test
  void properlyGetsAllAccounts() {
    List<Account> accounts = accountService.getAccountList();
    assertThat(accounts.isEmpty(), is(false));
    assertThat(accounts.size(), is(equalTo(1)));
    assertThat(accounts.get(0), is(equalTo(account)));
  }

  @Test
  void properlyGetsEmptyAccountList() throws Exception {
    teardown();
    List<Account> accounts = accountService.getAccountList();
    assertThat(accounts.isEmpty(), is(true));
  }

  @Test
  void properlyAddsNewAccessLevelToAccount()
          throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
    AccessLevel newAccessLevel = new Client();

    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), newAccessLevel);
    List<AccessLevel> accessLevels = accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels();
    assertThat(accessLevels.size(), equalTo(1));
    assertTrue(accessLevels.get(0) instanceof Client);
  }

  @Test
  void failsToAddAccessLevelWhenAccessLevelIsAdded()
          throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
    AccessLevel newAccessLevel = new Client();

    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), newAccessLevel);
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
    assertThrows(AccessLevelAlreadyAssignedException.class,
            () -> accountService.addAccessLevelToAccount(account.getId(), newAccessLevel));
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
  }

  @Test
  void failsToAddAnyAccessLevelLevelWhenAdministratorAccessLevelIsAlreadyAssigned() {
    AccessLevel administratorAccessLevel = new Administrator();
    AccessLevel clientAccessLevel = new Client();
    AccessLevel employeeAccessLevel = new Employee();
    AccessLevel salesRepAccessLevel = new SalesRep();

    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), administratorAccessLevel);
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));

    assertThrows(AdministratorAccessLevelAlreadyAssignedException.class,
            () -> accountService.addAccessLevelToAccount(account.getId(), administratorAccessLevel));
    assertThrows(AdministratorAccessLevelAlreadyAssignedException.class,
            () -> accountService.addAccessLevelToAccount(account.getId(), clientAccessLevel));
    assertThrows(AdministratorAccessLevelAlreadyAssignedException.class,
            () -> accountService.addAccessLevelToAccount(account.getId(), employeeAccessLevel));
    assertThrows(AdministratorAccessLevelAlreadyAssignedException.class,
            () -> accountService.addAccessLevelToAccount(account.getId(), salesRepAccessLevel));
  }

  @Test
  void failsToAddSalesRepAccessLevelWhenClientAccessLevelIsAlreadyAssigned() {
    AccessLevel clientAccessLevel = new Client();
    AccessLevel salesRepAccessLevel = new SalesRep();

    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), clientAccessLevel);
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));

    assertThrows(ClientAndSalesRepAccessLevelsConflictException.class,
            () -> accountService.addAccessLevelToAccount(account.getId(), salesRepAccessLevel));
  }

  @Test
  void failsToAddClientAccessLevelWhenSalesRepAccessLevelIsAlreadyAssigned() {
    AccessLevel clientAccessLevel = new Client();
    AccessLevel salesRepAccessLevel = new SalesRep();

    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), salesRepAccessLevel);
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));

    assertThrows(ClientAndSalesRepAccessLevelsConflictException.class,
            () -> accountService.addAccessLevelToAccount(account.getId(), clientAccessLevel));
  }

  @Test
  void properlyChangeAccessLevelWhenItsAlreadyOne() {
    AccessLevel oldAccessLevel = new Client();
    AccessLevel newAccessLevel = new Administrator();


    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), oldAccessLevel);
    List<AccessLevel> accessLevels = accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels();
    assertEquals(1, accessLevels.size());
    assertTrue(accessLevels.get(0) instanceof Client);

    assertDoesNotThrow(() -> accountService.changeAccessLevel(account.getId(), newAccessLevel));
    List<AccessLevel> newAccessLevels = accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels();
    assertEquals(1, newAccessLevels.size());
    assertTrue(newAccessLevels.get(0) instanceof Administrator);
  }

  @Test
  void failsToChangeAccessLevelWhenItsMoreThanOneAssigned() {
    AccessLevel oldAccessLevel1 = new Client();
    AccessLevel oldAccessLevel2 = new Employee();
    AccessLevel newAccessLevel = new Administrator();

    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), oldAccessLevel1);
    accountService.addAccessLevelToAccount(account.getId(), oldAccessLevel2);
    List<AccessLevel> accessLevels = accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels();
    assertEquals(2, accessLevels.size());

    assertThrows(MoreThanOneAccessLevelAssignedException.class, () -> {
      accountService.changeAccessLevel(account.getId(), newAccessLevel);
    });
  }

  @Test
  void properlyRemovesAccessLevelFromAccount()
      throws Exception {
    AccessLevel employeeAccessLevel = new Employee();
    AccessLevel clientAccessLevel = new Client();

    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), employeeAccessLevel);
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
    admin.call(() -> {
      accountService.addAccessLevelToAccount(account.getId(), clientAccessLevel);
    });
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(2));
    admin.call(() -> {
      accountService.removeAccessLevelFromAccount(account.getId(), employeeAccessLevel);
    });
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
  }

  @Test
  void failsToRemoveAccessLevelWhenAccountHasZeroAccessLevelsAssigned() {
    AccessLevel accessLevelClient = new Client();
    client.call(() -> {
      assertThrows(RemoveAccessLevelException.class,
          () -> accountService.removeAccessLevelFromAccount(account.getId(), accessLevelClient));
    });
  }

  @Test
  void failsToRemoveAccessLevelWhenAccountHasOnlyOneAccessLevelAssigned() {
    AccessLevel accessLevelClient = new Client();
    accountService.addAccessLevelToAccount(account.getId(), accessLevelClient);
    assertThrows(RemoveAccessLevelException.class,
            () -> accountService.removeAccessLevelFromAccount(account.getId(), accessLevelClient));
  }

  @Test
  void failsToRemoveAccessLevelWhenAccessLevelIsNotAdded()
          throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
    AccessLevel accessLevelClient = new Client();
    AccessLevel accessLevelEmployee = new Employee();
    AccessLevel accessLevelAdmin = new Administrator();

    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
    accountService.addAccessLevelToAccount(account.getId(), accessLevelClient);
    accountService.addAccessLevelToAccount(account.getId(), accessLevelEmployee);
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(2));
    assertThrows(AccessLevelNotAssignedException.class,
            () -> accountService.removeAccessLevelFromAccount(account.getId(), accessLevelAdmin));
    assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(2));
  }

  @Test
  void properlyEditsAccountInfo() throws Exception {
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
            accountService.getAccountByLogin("test").orElseThrow().getPerson().getFirstName());
    assertEquals(account.getPerson().getLastName(), accountService.getAccountByLogin("test").orElseThrow().getPerson().getLastName());
    assertEquals(account.getPerson().getAddress().getStreetNumber(),
            accountService.getAccountByLogin("test").orElseThrow().getPerson().getAddress().getStreetNumber());
    accountService.editAccountInfo(account.getLogin(), editedAccount, CryptHashUtils.hashVersion(account.getSumOfVersions()));
    utx.commit();

    utx.begin();
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getFirstName(), "Adam");
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getLastName(), "John");
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getAddress().getStreetNumber(), 24);
    utx.commit();
  }


  @Test
  void properlyEditsAccountInfoAsAdmin() throws Exception {
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
            accountService.getAccountByLogin("test").orElseThrow().getPerson().getFirstName());
    assertEquals(account.getPerson().getLastName(), accountService.getAccountByLogin("test").orElseThrow().getPerson().getLastName());
    assertEquals(account.getPerson().getAddress().getStreetNumber(),
            accountService.getAccountByLogin("test").orElseThrow().getPerson().getAddress().getStreetNumber());
    assertEquals(account.getEmail(), accountService.getAccountByLogin("test").orElseThrow().getEmail());
    accountService.editAccountInfoAsAdmin(account.getLogin(), editedAccount, CryptHashUtils.hashVersion(account.getSumOfVersions()));
    utx.commit();

    utx.begin();
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getFirstName(), "Jack");
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getLastName(), "Smith");
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getAddress().getCountry(), "Poland");
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getAddress().getCity(), "Warsaw");
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getAddress().getStreet(), "Mickiewicza");
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getAddress().getPostalCode(), "92-100");
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getPerson().getAddress().getStreetNumber(), 15);
    assertEquals(accountService.getAccountByLogin("test").orElseThrow().getEmail(), "test1@gmail.com");
    utx.commit();
  }

  @Test
  void properlyRegistersAccount() {
    assertDoesNotThrow(() -> accountService.registerAccount(accountToRegister));
    Account account = accountService.getAccountByLogin("test123").orElseThrow();
    assertEquals(2, accountService.getAccountList().size());
    assertEquals(AccountState.NOT_VERIFIED, account.getAccountState());
    assertEquals(false, account.getArchive());
    assertEquals(0, account.getFailedLoginCounter());
  }

  @Test
  void properlyCreatesAccount() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    accountToRegister.setAccountState(AccountState.ACTIVE);
    AccessLevel client = new Client();
    AccessLevel employee = new Employee();
    accountService.createAccount(accountToRegister);

    Account account = accountService.getAccountByLogin("test123").orElseThrow();
    accountToRegister.setAccessLevels(List.of(client,employee));
    assertEquals(2, accountService.getAccountList().size());
    assertEquals(AccountState.ACTIVE, account.getAccountState());
    assertEquals(false, account.getArchive());
    assertEquals(0, account.getFailedLoginCounter());
  }

  @Test
  void failsToRegisterAccountWithSameLogin() {
    accountToRegister.setLogin(account.getLogin());
    assertThrows(Exception.class, () -> accountService.registerAccount(accountToRegister));
    assertEquals(1, accountService.getAccountList().size());
  }

  @Test
  void properlyChangesPassword() {
    String newPassword = "newPassword";
    assertEquals(account.getPassword(),
            accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
    assertTrue(CryptHashUtils.verifyPassword("test", account.getPassword()));
    assertDoesNotThrow(() -> accountService.changePassword(account.getLogin(), newPassword,"test"));
    Account updateAccount = accountService.getAccountByLogin(account.getLogin()).orElseThrow();

    assertFalse(CryptHashUtils.verifyPassword("test", updateAccount.getPassword()));
    assertTrue(CryptHashUtils.verifyPassword(newPassword, updateAccount.getPassword()));
  }

  @Test
  void failsToChangePasswordWhenGivenOldPassword() throws AccountNotFoundException {
    String oldPassword = account.getPassword();
    assertEquals(oldPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
    assertThrows(OldPasswordGivenException.class,
            () -> accountService.changePassword(account.getLogin(), "test", "test"));

  }

  @Test
  void properlyChangesPasswordAsAdmin() {
    String newPassword = "newPassword";
    assertEquals(account.getPassword(),
            accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
    assertDoesNotThrow(() -> accountService.changePasswordAsAdmin(account.getLogin(), newPassword));
    assertEquals(newPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
  }

  @Test
  void failsToChangePasswordAsAdminWhenGivenOldPassword() throws AccountNotFoundException {
    String oldPassword = account.getPassword();
    assertEquals(oldPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
    accountService.changePasswordAsAdmin(account.getLogin(), oldPassword);
    assertEquals(oldPassword, accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
  }

  @Test
  void properlyBlocksActiveAccount() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    assertEquals(AccountState.ACTIVE, account.getAccountState());
    utx.begin();
    assertDoesNotThrow(() -> accountService.blockAccount(account.getId()));
    Optional<Account> edited = accountService.getAccountByLogin(account.getLogin());
    assertEquals(AccountState.BLOCKED, edited.get().getAccountState());
    utx.commit();
  }

  @Test
  void blockAlreadyBlockedAccountShouldThrowException() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {

    utx.begin();
    accountService.createAccount(accountToRegister);
    accountService.blockAccount(accountToRegister.getId());
    utx.commit();
    assertThrows(IllegalAccountStateChangeException.class,
            () -> accountService.blockAccount(accountToRegister.getId()));
  }

  @Test
  void blockNonExistingUserShouldThrowException() {
    assertThrows(AccountNotFoundException.class,
            () -> accountService.blockAccount(Long.MAX_VALUE));
  }


  @Test
  void properlyActivatesBlockedAccount() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    accountService.createAccount(accountToRegister);
    accountService.blockAccount(accountToRegister.getId());
    utx.commit();
    assertEquals(AccountState.BLOCKED, accountService.getAccountById(accountToRegister.getId()).get().getAccountState());
    assertDoesNotThrow(() -> accountService.activateAccount(accountToRegister.getId()));
    Account changed = accountService.getAccountByLogin(accountToRegister.getLogin()).orElseThrow();
    assertEquals(AccountState.ACTIVE, changed.getAccountState());
  }

  @Test
  void activateAlreadyActivatedAccountShouldThrowException() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    accountToRegister.setAccountState(AccountState.ACTIVE);
    utx.begin();
    accountService.createAccount(accountToRegister);
    utx.commit();
    assertThrows(IllegalAccountStateChangeException.class,
            () -> accountService.activateAccount(accountToRegister.getId()));
  }

  @Test
  void activateInactiveAccountShouldThrowException() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    accountToRegister.setAccountState(AccountState.INACTIVE);
    utx.begin();
    accountService.createAccount(accountToRegister);
    utx.commit();
    assertThrows(IllegalAccountStateChangeException.class,
            () -> accountService.activateAccount(accountToRegister.getId()));
  }

  @Test
  void activateNonExistingAccountShouldThrowException() {
    assertThrows(AccountNotFoundException.class,
            () -> accountService.activateAccount(Long.MAX_VALUE));
  }

  @Test
  void properlyUpdateEmailWhenNewEmailIsSet() throws Exception {
    utx.begin();
    accountToRegister.setAccountState(AccountState.ACTIVE);
    accountToRegister.setNewEmail("newssbd02Email@gmail.com");

    accountService.createAccount(accountToRegister);
    assertEquals("test123@gmail.com", accountToRegister.getEmail());
    assertEquals("newssbd02Email@gmail.com", accountToRegister.getNewEmail());

    Account accountAfterUpdate = accountService.updateEmailAfterConfirmation(accountToRegister.getLogin());
    assertEquals("newssbd02Email@gmail.com", accountAfterUpdate.getEmail());
    assertNull(accountAfterUpdate.getNewEmail());
    utx.commit();
  }
}

//
//@Stateless
//@RunAs("Administrator")
//@PermitAll
//class AdministratorArquillianConfig {
//    public <V> V call(Callable<V> callable) throws Exception {
//        return callable.call();
//    }
//}

//
//@Stateful
//@Specializes
//class AccountServiceTestConfiguration extends AccountService {
//
//}
