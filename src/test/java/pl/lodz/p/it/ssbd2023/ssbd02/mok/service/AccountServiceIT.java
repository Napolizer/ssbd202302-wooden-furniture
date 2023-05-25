package pl.lodz.p.it.ssbd2023.ssbd02.mok.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBAccessException;
import jakarta.ejb.EJBException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AccountServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.MailServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.TokenServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.AdminAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.ClientAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.TokenService;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@ExtendWith(ArquillianExtension.class)
public class AccountServiceIT {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
  @Resource
  private UserTransaction utx;
  @Inject
  private AccountServiceOperations accountService;
  @Inject
  private AdminAuth admin;
  @Inject
  private ClientAuth client;

  @Inject
  private TokenServiceOperations tokenService;

  private Account account;
  private Account accountToRegister;

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class)
            .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
            .addPackages(true, "org.postgresql")
            .addPackages(true, "org.hamcrest")
            .addPackages(true, "org.apache")
            .addPackages(true, "at.favre.lib")
            .addPackages(true, "io.jsonwebtoken")
            .addPackages(true, "javax.xml.bind")
            .addAsResource(new File("src/test/resources/"), "")
            .addAsWebInfResource(new File("src/test/resources/WEB-INF/glassfish-web.xml"), "glassfish-web.xml");
  }

  @BeforeEach
  public void setup() {
    admin.call(() -> {
      account = Account.builder()
          .login("Administrator")
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
          .newEmail("newemail123@gmail.com")
          .accountState(AccountState.ACTIVE)
          .timeZone(TimeZone.EUROPE_WARSAW)
          .forcePasswordChange(true)
          .build();
      accountService.createAccount(account);
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
          .timeZone(TimeZone.EUROPE_WARSAW)
          .build();
    });
  }

  @AfterEach
  public void teardown() throws Exception {
    utx.begin();
    em.createNativeQuery("ALTER TABLE person DROP CONSTRAINT fk_person_created_by").executeUpdate();
    em.createNativeQuery("ALTER TABLE account DROP CONSTRAINT fk_account_created_by").executeUpdate();
    em.createNativeQuery("ALTER TABLE address DROP CONSTRAINT fk_address_created_by").executeUpdate();
    em.createNativeQuery("DELETE FROM sales_rep").executeUpdate();
    em.createNativeQuery("DELETE FROM password_history").executeUpdate();
    em.createNativeQuery("DELETE FROM administrator").executeUpdate();
    em.createNativeQuery("DELETE FROM client").executeUpdate();
    em.createNativeQuery("DELETE FROM employee").executeUpdate();
    em.createNativeQuery("DELETE FROM access_level").executeUpdate();
    em.createNativeQuery("DELETE FROM Account").executeUpdate();
    em.createNativeQuery("DELETE FROM Person").executeUpdate();
    em.createNativeQuery("DELETE FROM Address").executeUpdate();
    em.createNativeQuery("ALTER TABLE person ADD CONSTRAINT fk_person_created_by FOREIGN KEY (created_by) REFERENCES Account(id)").executeUpdate();
    em.createNativeQuery("ALTER TABLE account ADD CONSTRAINT fk_account_created_by FOREIGN KEY (created_by) REFERENCES Account(id)").executeUpdate();
    em.createNativeQuery("ALTER TABLE address ADD CONSTRAINT fk_address_created_by FOREIGN KEY (created_by) REFERENCES Account(id)").executeUpdate();
    utx.commit();
  }

  @Test
  void properlyGetsAccountByLogin() {
    client.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountByLogin(account.getLogin());
      assertThat(accountOptional.isPresent(), is(equalTo(true)));
      assertThat(accountOptional.get(), is(equalTo(account)));
    });
  }

  @Test
  void failsToGetAccountByEmailWhenEmailIsNull() {
    admin.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountByEmail(null);
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void failsToGetAccountByEmailWhenEmailIsEmpty() {
    admin.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountByEmail("");
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void failsToGetAccountByEmailWhenEmailDoesNotExist() {
    client.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountByLogin("nonexistent@gmail.com");
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void properlyGetsAccountById() {
    admin.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountById(account.getId());
      assertThat(accountOptional.isPresent(), is(equalTo(true)));
      assertThat(accountOptional.get(), is(equalTo(account)));
    });
  }

  @Test
  void failsToGetAccountByIdWhenIdDoesNotExist() {
    admin.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountById(0L);
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void failsToGetAccountByIdWhenIdIsNull() {
    admin.call(() -> {
      assertNull(accountService.getAccountById(null).orElse(null));
    });
  }

  @Test
  void failsToGetAccountByIdWhenIdIsNegative() {
    admin.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountById(-1L);
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void failsToGetAccountByIdWhenIdIsMaxLong() {
    admin.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountById(Long.MAX_VALUE);
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void failsToGetAccountByIdWhenIdIsMinLong() {
    admin.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountById(Long.MIN_VALUE);
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void properlyGetsAccountByEmail() {
    admin.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountByEmail(account.getEmail());
      assertThat(accountOptional.isPresent(), is(equalTo(true)));
      assertThat(accountOptional.get(), is(equalTo(account)));
    });
  }

  @Test
  void failsToGetAccountByLoginWhenLoginIsNull() {
    client.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountByLogin(null);
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void failsToGetAccountByLoginWhenLoginIsEmpty() {
    client.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountByLogin("");
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void failsToGetAccountByLoginWhenLoginDoesNotExist() {
    client.call(() -> {
      Optional<Account> accountOptional = accountService.getAccountByLogin("nonexistent");
      assertThat(accountOptional.isPresent(), is(equalTo(false)));
    });
  }

  @Test
  void properlyGetsAllAccounts() {
    admin.call(() -> {
      List<Account> accounts = accountService.getAccountList();
      assertThat(accounts.isEmpty(), is(false));
      assertThat(accounts.size(), is(equalTo(1)));
      assertThat(accounts.get(0), is(equalTo(account)));
    });
  }

  @Test
  void properlyGetsEmptyAccountList() throws Exception {
    teardown();
    admin.call(() -> {
      List<Account> accounts = accountService.getAccountList();
      assertThat(accounts.isEmpty(), is(true));
    });
  }

  @Test
  void properlyAddsNewAccessLevelToAccount()
          throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
    AccessLevel newAccessLevel = new Client();

    admin.call(() -> {
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
      accountService.addAccessLevelToAccount(account.getId(), newAccessLevel);
      List<AccessLevel> accessLevels = accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels();
      assertThat(accessLevels.size(), equalTo(1));
      assertTrue(accessLevels.get(0) instanceof Client);
    });
  }

  @Test
  void failsToAddAccessLevelWhenAccessLevelIsAdded()
          throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
    AccessLevel newAccessLevel = new Client();

    admin.call(() -> {
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
      accountService.addAccessLevelToAccount(account.getId(), newAccessLevel);
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
      assertThrows(AccessLevelAlreadyAssignedException.class,
          () -> accountService.addAccessLevelToAccount(account.getId(), newAccessLevel));
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));
    });
  }

  @Test
  void failsToAddAnyAccessLevelLevelWhenAdministratorAccessLevelIsAlreadyAssigned() {
    AccessLevel administratorAccessLevel = new Administrator();
    AccessLevel clientAccessLevel = new Client();
    AccessLevel employeeAccessLevel = new Employee();
    AccessLevel salesRepAccessLevel = new SalesRep();

    admin.call(() -> {
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
    });
  }

  @Test
  void failsToAddSalesRepAccessLevelWhenClientAccessLevelIsAlreadyAssigned() {
    AccessLevel clientAccessLevel = new Client();
    AccessLevel salesRepAccessLevel = new SalesRep();

    admin.call(() -> {
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
      accountService.addAccessLevelToAccount(account.getId(), clientAccessLevel);
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));

      assertThrows(ClientAndSalesRepAccessLevelsConflictException.class,
          () -> accountService.addAccessLevelToAccount(account.getId(), salesRepAccessLevel));
    });
  }

  @Test
  void failsToAddClientAccessLevelWhenSalesRepAccessLevelIsAlreadyAssigned() {
    AccessLevel clientAccessLevel = new Client();
    AccessLevel salesRepAccessLevel = new SalesRep();

    admin.call(() -> {
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
      accountService.addAccessLevelToAccount(account.getId(), salesRepAccessLevel);
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(1));

      assertThrows(ClientAndSalesRepAccessLevelsConflictException.class,
          () -> accountService.addAccessLevelToAccount(account.getId(), clientAccessLevel));
    });
  }

  @Test
  void properlyChangeAccessLevelWhenItsAlreadyOne() {
    AccessLevel oldAccessLevel = new Client();
    AccessLevel newAccessLevel = new Administrator();


    admin.call(() -> {
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
      accountService.addAccessLevelToAccount(account.getId(), oldAccessLevel);
      List<AccessLevel> accessLevels = accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels();
      assertEquals(1, accessLevels.size());
      assertTrue(accessLevels.get(0) instanceof Client);

      assertDoesNotThrow(() -> accountService.changeAccessLevel(account.getId(), newAccessLevel));
      List<AccessLevel> newAccessLevels =
          accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels();
      assertEquals(1, newAccessLevels.size());
      assertTrue(newAccessLevels.get(0) instanceof Administrator);
    });
  }

  @Test
  void failsToChangeAccessLevelWhenItsMoreThanOneAssigned() {
    AccessLevel oldAccessLevel1 = new Client();
    AccessLevel oldAccessLevel2 = new Employee();
    AccessLevel newAccessLevel = new Administrator();

    admin.call(() -> {
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
      accountService.addAccessLevelToAccount(account.getId(), oldAccessLevel1);
      accountService.addAccessLevelToAccount(account.getId(), oldAccessLevel2);
      List<AccessLevel> accessLevels = accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels();
      assertEquals(2, accessLevels.size());

      assertThrows(MoreThanOneAccessLevelAssignedException.class, () -> {
        accountService.changeAccessLevel(account.getId(), newAccessLevel);
      });
    });
  }

  @Test
  void properlyRemovesAccessLevelFromAccount()
      throws Exception {
    AccessLevel employeeAccessLevel = new Employee();
    AccessLevel clientAccessLevel = new Client();

    admin.call(() -> {
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
    });
  }

  @Test
  void failsToRemoveAccessLevelWhenAccountHasOnlyOneAccessLevelAssigned() {
    AccessLevel accessLevelClient = new Client();
    admin.call(() -> {
      accountService.addAccessLevelToAccount(account.getId(), accessLevelClient);
      assertThrows(RemoveAccessLevelException.class,
          () -> accountService.removeAccessLevelFromAccount(account.getId(), accessLevelClient));
    });
  }

  @Test
  void failsToRemoveAccessLevelWhenAccessLevelIsNotAdded()
          throws AccessLevelAlreadyAssignedException, AccountNotFoundException {
    AccessLevel accessLevelClient = new Client();
    AccessLevel accessLevelEmployee = new Employee();
    AccessLevel accessLevelAdmin = new Administrator();

    admin.call(() -> {
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(0));
      accountService.addAccessLevelToAccount(account.getId(), accessLevelClient);
      accountService.addAccessLevelToAccount(account.getId(), accessLevelEmployee);
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(2));
      assertThrows(AccessLevelNotAssignedException.class,
          () -> accountService.removeAccessLevelFromAccount(account.getId(), accessLevelAdmin));
      assertThat(accountService.getAccountById(account.getId()).orElseThrow().getAccessLevels().size(), equalTo(2));
    });
  }

  @Test
  void properlyRegistersAccountAndConfirm() {
    assertDoesNotThrow(() -> accountService.registerAccount(accountToRegister));
    admin.call(() -> {
      Account account = accountService.getAccountByLogin("test123").orElseThrow();
      assertEquals(2, accountService.getAccountList().size());
      assertEquals(AccountState.NOT_VERIFIED, account.getAccountState());
      assertEquals(false, account.getArchive());
      assertEquals(0, account.getFailedLoginCounter());

      String token = tokenService.generateTokenForEmailLink(account,
          TokenType.ACCOUNT_CONFIRMATION);

      assertDoesNotThrow(() -> accountService.confirmAccount(token));
      Account updated = accountService.getAccountByLogin("test123").orElseThrow();
      assertEquals(AccountState.ACTIVE, updated.getAccountState());
    });
  }


  @Test
  void failsToRegisterAccountWithSameEmail() {
    admin.call(() -> {
      accountToRegister.setEmail(account.getEmail());
      assertThrows(BaseWebApplicationException.class, () -> accountService.registerAccount(accountToRegister));
      assertEquals(1, accountService.getAccountList().size());
    });
  }

  @Test
  void properlyCreatesAccount() {
    accountToRegister.setAccountState(AccountState.ACTIVE);
    AccessLevel client = new Client();
    AccessLevel employee = new Employee();
    admin.call(() -> {
      accountService.createAccount(accountToRegister);

      Account account = accountService.getAccountByLogin("test123").orElseThrow();
      accountToRegister.setAccessLevels(List.of(client, employee));
      assertEquals(2, accountService.getAccountList().size());
      assertEquals(AccountState.ACTIVE, account.getAccountState());
      assertEquals(false, account.getArchive());
      assertEquals(0, account.getFailedLoginCounter());
    });
  }

  @Test
  void failsToRegisterAccountWithSameLogin() {
    accountToRegister.setLogin(account.getLogin());
    admin.call(() -> {
      assertThrows(Exception.class, () -> accountService.registerAccount(accountToRegister));
      assertEquals(1, accountService.getAccountList().size());
    });
  }

  @Test
  void properlyChangesPassword() {
    String newPassword = "newPassword";
    admin.call(() -> {
      assertEquals(account.getPassword(),
          accountService.getAccountByLogin(account.getLogin()).orElseThrow().getPassword());
      assertTrue(CryptHashUtils.verifyPassword("test", account.getPassword()));
      assertDoesNotThrow(() -> accountService.changePassword(account.getLogin(), newPassword, "test"));
      Account updateAccount = accountService.getAccountByLogin(account.getLogin()).orElseThrow();

      assertFalse(CryptHashUtils.verifyPassword("test", updateAccount.getPassword()));
      assertTrue(CryptHashUtils.verifyPassword(newPassword, updateAccount.getPassword()));
    });
  }
  @Test
  void properlyBlocksActiveAccount() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    assertEquals(AccountState.ACTIVE, account.getAccountState());
    utx.begin();
    admin.call(() -> {
      assertDoesNotThrow(() -> accountService.blockAccount(account.getId()));
      Optional<Account> edited = accountService.getAccountByLogin(account.getLogin());
      assertEquals(AccountState.BLOCKED, edited.get().getAccountState());
    });
    utx.commit();
  }

  @Test
  void blockAlreadyBlockedAccountShouldThrowException() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {

    utx.begin();
    admin.call(() -> {
          accountService.createAccount(accountToRegister);
          accountService.blockAccount(accountToRegister.getId());
        });
    utx.commit();
    admin.call(() -> {
      assertThrows(IllegalAccountStateChangeException.class,
          () -> accountService.blockAccount(accountToRegister.getId()));
    });
  }

  @Test
  void blockNonExistingUserShouldThrowException() {
    admin.call(() -> {
      assertThrows(AccountNotFoundException.class,
          () -> accountService.blockAccount(Long.MAX_VALUE));
    });
  }


  @Test
  void properlyActivatesBlockedAccount() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    admin.call(() -> {
          accountService.createAccount(accountToRegister);
          accountService.blockAccount(accountToRegister.getId());
        });
    utx.commit();
    admin.call(() -> {
      assertEquals(AccountState.BLOCKED,
          accountService.getAccountById(accountToRegister.getId()).get().getAccountState());
      assertDoesNotThrow(() -> accountService.activateAccount(accountToRegister.getId()));
      Account changed = accountService.getAccountByLogin(accountToRegister.getLogin()).orElseThrow();
      assertEquals(AccountState.ACTIVE, changed.getAccountState());
    });
  }

  @Test
  void activateAlreadyActivatedAccountShouldThrowException() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    accountToRegister.setAccountState(AccountState.ACTIVE);
    utx.begin();
    admin.call(() -> {
      accountService.createAccount(accountToRegister);
    });
    utx.commit();
    admin.call(() -> {
      assertThrows(IllegalAccountStateChangeException.class,
          () -> accountService.activateAccount(accountToRegister.getId()));
    });
  }

  @Test
  void activateInactiveAccountShouldThrowException() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
    accountToRegister.setAccountState(AccountState.INACTIVE);
    utx.begin();
    admin.call(() -> {
      accountService.createAccount(accountToRegister);
    });
    utx.commit();
    admin.call(() -> {
      assertThrows(IllegalAccountStateChangeException.class,
          () -> accountService.activateAccount(accountToRegister.getId()));
    });
  }

  @Test
  void activateNonExistingAccountShouldThrowException() {
    admin.call(() -> {
      assertThrows(AccountNotFoundException.class,
          () -> accountService.activateAccount(Long.MAX_VALUE));
    });
  }

  @Test
  void properlyUpdateEmailWhenNewEmailIsSet() throws Exception {
    utx.begin();
    admin.call(() -> {
      accountToRegister.setAccountState(AccountState.ACTIVE);
      accountToRegister.setNewEmail("newssbd02Email@gmail.com");

      accountService.createAccount(accountToRegister);
      assertEquals("test123@gmail.com", accountToRegister.getEmail());
      assertEquals("newssbd02Email@gmail.com", accountToRegister.getNewEmail());

      Account accountAfterUpdate = accountService.updateEmailAfterConfirmation(accountToRegister.getLogin());
      assertEquals("newssbd02Email@gmail.com", accountAfterUpdate.getEmail());
      assertNull(accountAfterUpdate.getNewEmail());
    });
    utx.commit();
  }

  @Test
  void properlyResetsPassword() {
    String newPassword = "NewPassword123!";
    assertDoesNotThrow(() -> accountService.resetPassword(account.getLogin(), newPassword));
    admin.call(() -> {
      Account updated = accountService.getAccountByLogin(account.getLogin()).orElseThrow();
      assertTrue(CryptHashUtils.verifyPassword(newPassword, updated.getPassword()));
    });
  }

  @Test
  void properlyGeneratesTokenFromRefresh() {
    admin.call(() -> {
      account.setLogin("Administrator");
      String refreshToken = tokenService.generateRefreshToken(account);
      String generatedToken = accountService.generateTokenFromRefresh(refreshToken);
      assertThat(generatedToken, is(notNullValue()));
      assertThat(tokenService.getTokenClaims(generatedToken).getLogin(), is(equalTo(account.getLogin())));
      assertThat(tokenService.getTokenClaims(generatedToken).getAccessLevels(), is(equalTo(account.getAccessLevels())));
    });
  }

  @Test
  public void properlyPersistsOldPasswordDuringResetPassword() {
    String newPassword = "NewPassword123!";
    String oldHash = account.getPassword();
    assertDoesNotThrow(() -> accountService.resetPassword(account.getLogin(), newPassword));
    admin.call(() -> {
      Account updated = accountService.getAccountByLogin(account.getLogin()).orElseThrow();
      assertTrue(CryptHashUtils.verifyPassword(newPassword, updated.getPassword()));
      assertEquals(updated.getPasswordHistory().get(0).getHash(), oldHash);
    });
  }

  @Test
  public void properlyPersistsOldPasswordDuringChangePassword() {
    String newPassword = "NewPassword123!";
    String oldHash = account.getPassword();
    admin.call(() -> {
      assertDoesNotThrow(() -> accountService.changePassword(account.getLogin(), newPassword, "test"));
      Account updated = accountService.getAccountByLogin(account.getLogin()).orElseThrow();
      assertEquals(updated.getPasswordHistory().get(0).getHash(), oldHash);
    });
  }

  @Test
  public void properlyPersistsOldPasswordDuringPasswordChangeFromLink() {
    String newPassword = "NewPassword123!";
    String oldHash = account.getPassword();
    String token = tokenService.generateTokenForEmailLink(account, TokenType.CHANGE_EMAIL);
    admin.call(() -> {
      assertDoesNotThrow(() -> accountService.changePasswordFromLink(token, newPassword, "test"));
      Account updated = accountService.getAccountByLogin(account.getLogin()).orElseThrow();
      assertEquals(updated.getPasswordHistory().get(0).getHash(), oldHash);
    });
  }

  @Test
  public void properlyChangesForcePasswordChangeFieldDuringResetPassword() {
    String newPassword = "NewPassword123!";
    assertDoesNotThrow(() -> accountService.resetPassword(account.getLogin(), newPassword));
    admin.call(() -> {
      Account updated = accountService.getAccountByLogin(account.getLogin()).orElseThrow();
      assertEquals(false, updated.getForcePasswordChange());
    });
  }

  @Test
  public void properlyChangesForcePasswordChangeFieldDuringChangePassword() {
    String newPassword = "NewPassword123!";
    admin.call(() -> {
      assertDoesNotThrow(() -> accountService.changePassword(account.getLogin(), newPassword, "test"));
      Account updated = accountService.getAccountByLogin(account.getLogin()).orElseThrow();
      assertEquals(false, updated.getForcePasswordChange());
    });
  }

  @Test
  public void properlyChangesForcePasswordChangeFieldDuringPasswordChangeFromLink() {
    String newPassword = "NewPassword123!";
    String token = tokenService.generateTokenForEmailLink(account, TokenType.CHANGE_EMAIL);
    admin.call(() -> {
      assertDoesNotThrow(() -> accountService.changePasswordFromLink(token, newPassword, "test"));
      Account updated = accountService.getAccountByLogin(account.getLogin()).orElseThrow();
      assertEquals(false, updated.getForcePasswordChange());
    });
  }

  @ParameterizedTest(name= "invalid refresh token: {0}")
  @CsvSource({
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjb",
      "123",
      "asda"
  })
  void shouldFailToGenerateRefreshTokenIfRefreshTokenIsInvalid(String invalidRefreshToken) {
    client.call(() -> {
      assertThrows(BaseWebApplicationException.class, () -> accountService.generateTokenFromRefresh(invalidRefreshToken));
    });
  }

  @ParameterizedTest(name= "expired refresh token: {0}")
  @CsvSource({
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmlzdHJhdG9yIiwiaWF0IjoxNjg0Nzc1NjMzLCJleHAiOjE2ODQ4NjIwMzN9.sMj1YDOyxayETaJ3Ry60E1K6Y1CgTvfguu2LFtLq1E8gtx_mPR_DpZ6Qj77qGnZk_nHQA3SiwmjqdVoQ0TueUQ",
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmlzdHJhdG9yIiwiaWF0IjoxNjg0Nzc1NzExLCJleHAiOjE2ODQ4NjIxMTF9.mbM5mCxAlMEE3bFGbX05A72IX4MKF7zyrVXN2TZ1xbzzjD7gx0x9Egc608o1L-NtW5kAE2hwiPqs0NLrGPgjzA",
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmlzdHJhdG9yIiwiaWF0IjoxNjg0Nzc1NzIxLCJleHAiOjE2ODQ4NjIxMjF9.wpKcillrdKaMsv_xYZmaQj635HPAz5my5ZezJXrS235IkED8JFVDKuduF81nGgranWCi7no0sxVYIVa-O3fp-g"
  })
  void shouldFailToGenerateRefreshTokenIfRefreshTokenIsExpired(String expiredRefreshToken) {
    client.call(() -> {
      assertThrows(BaseWebApplicationException.class, () -> accountService.generateTokenFromRefresh(expiredRefreshToken));
    });
  }

  @Test
  void shouldFailToGenerateRefreshTokenForAnotherUser() {
    client.call(() -> {
      String refreshToken = tokenService.generateRefreshToken(account);
      assertThrows(BaseWebApplicationException.class, () -> accountService.generateTokenFromRefresh(refreshToken));
    });
  }

  @Test
  void properlyChangesMode() {
    admin.call(() -> {
      accountService.changeMode(account.getLogin(), Mode.DARK);
      assertEquals(Mode.DARK, accountService.getAccountById(account.getId()).get().getMode());
      accountService.changeMode(account.getLogin(), Mode.LIGHT);
      assertEquals(Mode.LIGHT, accountService.getAccountById(account.getId()).get().getMode());
    });
  }

  @Test
  void shouldFailToChangeModeWithoutAuthentication() {
    assertThrows(EJBAccessException.class, () -> accountService.changeMode(account.getLogin(), Mode.DARK));
  }
}
