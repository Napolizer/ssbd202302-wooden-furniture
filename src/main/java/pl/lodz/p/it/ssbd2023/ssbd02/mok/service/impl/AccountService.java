package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.OptimisticLockException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.config.enums.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountSearchSettings;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Mode;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.PasswordHistory;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AccountServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.EmailSendingRetryServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.MailServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.TokenServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericServiceExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class AccountService extends AbstractService implements AccountServiceOperations {
  @Inject
  private AccountFacadeOperations accountFacade;
  @Inject
  private MailServiceOperations mailService;
  @Inject
  private TokenServiceOperations tokenService;
  @Inject
  private EmailSendingRetryServiceOperations emailSendingRetryService;
  @Inject
  private Principal principal;

  @PermitAll
  public Optional<Account> getAccountByLogin(String login) {
    return accountFacade.findByLogin(login);
  }

  @RolesAllowed(ADMINISTRATOR)
  public Optional<Account> getAccountById(Long id) {
    return accountFacade.findById(id);
  }

  @PermitAll
  public Optional<Account> getAccountByEmail(String email) {
    return accountFacade.findByEmail(email);
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<Account> getAccountList() {
    return accountFacade.findAll();
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) {
    Account foundAccount =
        accountFacade.findById(accountId).orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    if (!foundAccount.getAccountState().equals(AccountState.ACTIVE)) {
      throw ApplicationExceptionFactory.createAccountNotActiveException();
    }

    List<AccessLevel> accessLevels = foundAccount.getAccessLevels();

    if ((accessLevels.size() > 0 && Objects.equals(accessLevel.getRoleName(), ADMINISTRATOR))) {
      throw ApplicationExceptionFactory.createAdministratorAccessLevelAlreadyAssignedException();
    }

    for (AccessLevel item : accessLevels) {
      if (Objects.equals(item.getRoleName(), ADMINISTRATOR)) {
        throw ApplicationExceptionFactory.createAdministratorAccessLevelAlreadyAssignedException();
      }

      if (Objects.equals(item.getRoleName(), accessLevel.getRoleName())) {
        throw ApplicationExceptionFactory.createAccessLevelAlreadyAssignedException();
      }

      if ((Objects.equals(item.getRoleName(), CLIENT) && Objects.equals(accessLevel.getRoleName(), SALES_REP))
          || (Objects.equals(item.getRoleName(), SALES_REP)
            && Objects.equals(accessLevel.getRoleName(), CLIENT))) {
        throw ApplicationExceptionFactory.createClientAndSalesRepAccessLevelsConflictException();
      }
    }

    accessLevel.setAccount(foundAccount);
    accessLevels.add(accessLevel);
    foundAccount.setAccessLevels(accessLevels);

    Account updatedAccount = accountFacade.update(foundAccount);
    mailService.sendEmailAboutAddingAccessLevel(foundAccount.getEmail(), foundAccount.getLocale(),
            accessLevel.getRoleName());
    return updatedAccount;
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) {
    Account foundAccount = accountFacade.findById(accountId)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
    List<AccessLevel> accessLevels = foundAccount.getAccessLevels();

    if (accessLevels.size() <= 1) {
      throw ApplicationExceptionFactory.createRemoveAccessLevelException();
    }

    for (AccessLevel item : accessLevels) {
      if (item.getClass() == accessLevel.getClass()) {
        accessLevels.remove(item);
        foundAccount.setAccessLevels(accessLevels);
        Account updatedAccount = accountFacade.update(foundAccount);
        mailService.sendEmailAboutRemovingAccessLevel(foundAccount.getEmail(),
                foundAccount.getLocale(), item.getRoleName());
        return updatedAccount;
      }
    }
    throw ApplicationExceptionFactory.createAccessLevelNotAssignedException();
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account changeAccessLevel(Long accountId, AccessLevel accessLevel) {
    Account account = accountFacade.findById(accountId).orElseThrow(AccountNotFoundException::new);
    List<AccessLevel> accessLevels = account.getAccessLevels();

    if (accessLevels.size() == 1) {
      AccessLevel oldAccessLevel = accessLevels.get(0);
      if (!Objects.equals(oldAccessLevel.getRoleName(), accessLevel.getRoleName())) {
        accessLevels.set(0, accessLevel);
        accessLevel.setAccount(account);
        account.setAccessLevels(accessLevels);
        accountFacade.update(account);
        mailService.sendEmailAboutChangingAccessLevel(account.getEmail(),
                account.getLocale(), oldAccessLevel.getRoleName(), accessLevel.getRoleName());

      } else {
        throw ApplicationExceptionFactory.createAccessLevelAlreadyAssignedException();
      }
      return account;
    }
    throw ApplicationExceptionFactory.createMoreThanOneAccessLevelAssignedException();
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Account editAccountInfo(String login, Account accountWithChanges, String hash) {
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    if (!CryptHashUtils.verifyVersion(account.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    account.update(accountWithChanges);
    return accountFacade.update(account);
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account editAccountInfoAsAdmin(String login, Account accountWithChanges, String hash) {
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    if (!CryptHashUtils.verifyVersion(account.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    account.update(accountWithChanges);
    return accountFacade.update(account);
  }

  @PermitAll
  public void registerAccount(Account account) {
    account.setFailedLoginCounter(0);
    account.setAccountState(AccountState.NOT_VERIFIED);
    account.setPassword(CryptHashUtils.hashPassword(account.getPassword()));
    account.setAccountType(AccountType.NORMAL);
    account.setMode(Mode.LIGHT);
    Account persistedAccount = accountFacade.create(account);
    String accountConfirmationToken = tokenService.generateTokenForEmailLink(account, TokenType.ACCOUNT_CONFIRMATION);
    emailSendingRetryService.sendEmailTokenAfterHalfExpirationTime(account.getLogin(), null,
            TokenType.ACCOUNT_CONFIRMATION, accountConfirmationToken);
    mailService.sendEmailWithAccountConfirmationLink(account.getEmail(), account.getLocale(),
            accountConfirmationToken, account.getLogin());
  }

  @PermitAll
  public void registerGoogleAccount(Account account) {
    account.setFailedLoginCounter(0);
    account.setAccountState(AccountState.ACTIVE);
    account.setPassword(CryptHashUtils.hashPassword(account.getPassword()));
    account.setAccountType(AccountType.GOOGLE);
    accountFacade.create(account);
  }

  @PermitAll
  public void registerGithubAccount(Account account) {
    account.setFailedLoginCounter(0);
    account.setAccountState(AccountState.ACTIVE);
    account.setPassword(CryptHashUtils.hashPassword(account.getPassword()));
    account.setAccountType(AccountType.GITHUB);
    accountFacade.create(account);
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account createAccount(Account account) {
    account.setFailedLoginCounter(0);
    account.setAccountState(AccountState.ACTIVE);
    account.setAccountType(AccountType.NORMAL);
    account.setPassword(CryptHashUtils.hashPassword(account.getPassword()));
    account.setMode(Mode.LIGHT);
    account.setForcePasswordChange(true);
    return accountFacade.create(account);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Account changePassword(String login, String newPassword, String currentPassword) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

    if (!account.getAccountType().equals(AccountType.NORMAL)) {
      throw ApplicationExceptionFactory.createInvalidAccountTypeException();
    }

    if (!CryptHashUtils.verifyPassword(currentPassword, account.getPassword())) {
      throw ApplicationExceptionFactory.createInvalidCurrentPasswordException();
    }

    for (PasswordHistory oldPassword : account.getPasswordHistory()) {
      if (CryptHashUtils.verifyPassword(newPassword, oldPassword.getHash())) {
        throw ApplicationExceptionFactory.passwordAlreadyUsedException();
      }
    }

    if (!CryptHashUtils.verifyPassword(newPassword, account.getPassword())) {
      account.getPasswordHistory().add(new PasswordHistory(account.getPassword()));
      account.setPassword(CryptHashUtils.hashPassword(newPassword));
      account.setForcePasswordChange(false);
      return accountFacade.update(account);
    } else {
      throw ApplicationExceptionFactory.createOldPasswordGivenException();
    }
  }

  @PermitAll
  public Account changePasswordFromLink(String token, String newPassword, String currentPassword) {
    String login = tokenService.getLoginFromTokenWithoutValidating(token, TokenType.CHANGE_PASSWORD);

    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    tokenService.validateEmailToken(token, TokenType.CHANGE_PASSWORD, account.getPassword());

    if (!CryptHashUtils.verifyPassword(currentPassword, account.getPassword())) {
      throw ApplicationExceptionFactory.createInvalidCurrentPasswordException();
    }

    for (PasswordHistory oldPassword : account.getPasswordHistory()) {
      if (CryptHashUtils.verifyPassword(newPassword, oldPassword.getHash())) {
        throw ApplicationExceptionFactory.passwordAlreadyUsedException();
      }
    }

    if (!CryptHashUtils.verifyPassword(newPassword, account.getPassword())) {
      account.getPasswordHistory().add(new PasswordHistory(account.getPassword()));
      account.setPassword(CryptHashUtils.hashPassword(newPassword));
      account.setForcePasswordChange(false);
      return accountFacade.update(account);
    } else {
      throw ApplicationExceptionFactory.createOldPasswordGivenException();
    }
  }

  @RolesAllowed(ADMINISTRATOR)
  public void changePasswordAsAdmin(String login) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

    String changePasswordToken = tokenService.generateTokenForEmailLink(account, TokenType.CHANGE_PASSWORD);
    emailSendingRetryService.sendEmailTokenAfterHalfExpirationTime(account.getLogin(), account.getPassword(),
            TokenType.CHANGE_PASSWORD, changePasswordToken);
    account.setForcePasswordChange(true);
    accountFacade.update(account);

    mailService.sendEmailWithPasswordChangeLink(account.getEmail(), account.getLocale(), changePasswordToken);

  }

  @PermitAll
  public Account blockAccount(Long id) {
    Account account = accountFacade.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    if (!account.getAccountState().equals(AccountState.ACTIVE)) {
      throw ApplicationExceptionFactory.createIllegalAccountStateChangeException();
    }

    account.setAccountState(AccountState.BLOCKED);
    Account accountAfterUpdate = accountFacade.update(account);

    mailService.sendEmailWithInfoAboutBlockingAccount(account.getEmail(), account.getLocale());
    return accountAfterUpdate;
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account activateAccount(Long id) {
    Account account = accountFacade.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
    AccountState state = account.getAccountState();

    if (state.equals(AccountState.INACTIVE) || state.equals(AccountState.ACTIVE)) {
      throw ApplicationExceptionFactory.createIllegalAccountStateChangeException();
    }

    account.setAccountState(AccountState.ACTIVE);
    account.setBlockadeEnd(null);
    Account accountAfterUpdate = accountFacade.update(account);

    mailService.sendEmailWithInfoAboutActivatingAccount(account.getEmail(), account.getLocale());
    return accountAfterUpdate;
  }

  @PermitAll
  public void confirmAccount(String token) {
    String login = tokenService.validateEmailToken(token, TokenType.ACCOUNT_CONFIRMATION, null);
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountConfirmationExpiredLinkException);
    if (!account.getAccountState().equals(AccountState.NOT_VERIFIED)) {
      throw ApplicationExceptionFactory.createAccountAlreadyVerifiedException();
    }
    account.setAccountState(AccountState.ACTIVE);
    accountFacade.update(account);
    mailService.sendEmailWithInfoAboutConfirmingAccount(account.getEmail(), account.getLocale());
  }

  @PermitAll
  public String validatePasswordResetToken(String token) {
    String login = tokenService.getLoginFromTokenWithoutValidating(token, TokenType.PASSWORD_RESET);
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createPasswordResetExpiredLinkException);
    tokenService.validateEmailToken(token, TokenType.PASSWORD_RESET, account.getPassword());
    return login;
  }

  @PermitAll
  public String validatePasswordChangeToken(String token) {
    String login = tokenService.getLoginFromTokenWithoutValidating(token, TokenType.CHANGE_PASSWORD);
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createPasswordResetExpiredLinkException);
    tokenService.validateEmailToken(token, TokenType.CHANGE_PASSWORD, account.getPassword());
    return login;
  }

  @PermitAll
  public String validateChangeEmailToken(String token) {
    String login = tokenService.getLoginFromTokenWithoutValidating(token, TokenType.CHANGE_EMAIL);
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createChangeEmailExpiredLinkException);
    tokenService.validateEmailToken(token, TokenType.CHANGE_EMAIL, account.getNewEmail());
    return login;
  }

  @PermitAll
  public void resetPassword(String token, String password) {
    String login = tokenService.getLoginFromTokenWithoutValidating(token, TokenType.PASSWORD_RESET);

    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createPasswordResetExpiredLinkException);

    tokenService.validateEmailToken(token, TokenType.PASSWORD_RESET, account.getPassword());

    final String hash = CryptHashUtils.hashPassword(password);

    if (CryptHashUtils.verifyPassword(password, account.getPassword())) {
      throw ApplicationExceptionFactory.createOldPasswordGivenException();
    }

    for (PasswordHistory oldPassword : account.getPasswordHistory()) {
      if (CryptHashUtils.verifyPassword(password, oldPassword.getHash())) {
        throw ApplicationExceptionFactory.passwordAlreadyUsedException();
      }
    }

    account.getPasswordHistory().add(new PasswordHistory(account.getPassword()));
    account.setPassword(hash);
    account.setForcePasswordChange(false);
    accountFacade.update(account);
  }

  @PermitAll
  public void sendResetPasswordEmail(String email) {
    Account account = accountFacade.findByEmail(email)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    if (account.getAccountState() != AccountState.ACTIVE) {
      throw ApplicationExceptionFactory.createAccountNotActiveException();
    }

    if (!account.getAccountType().equals(AccountType.NORMAL)) {
      throw ApplicationExceptionFactory.createInvalidAccountTypeException();
    }
    String resetPasswordToken = tokenService.generateTokenForEmailLink(account, TokenType.PASSWORD_RESET);
    emailSendingRetryService.sendEmailTokenAfterHalfExpirationTime(account.getLogin(), account.getPassword(),
            TokenType.PASSWORD_RESET, resetPasswordToken);

    mailService.sendResetPasswordEmail(account.getEmail(), account.getLocale(), resetPasswordToken);

  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Account updateEmailAfterConfirmation(String token) {
    String login = tokenService
            .getLoginFromTokenWithoutValidating(token, TokenType.CHANGE_EMAIL);

    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    tokenService.validateEmailToken(token, TokenType.CHANGE_EMAIL, account.getNewEmail());

    account.setEmail(account.getNewEmail());
    account.setNewEmail(null);
    return accountFacade.update(account);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void changeEmail(String newEmail, Long accountId, String principal, String version) {
    Account subject = accountFacade.findByLogin(principal)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    if (accountFacade.findByEmail(newEmail).isPresent()) {
      throw ApplicationExceptionFactory.createEmailAlreadyExistsException();
    }

    boolean isAdmin = subject.getAccessLevels()
            .stream().anyMatch(al -> al.getRoleName().equals(ADMINISTRATOR));

    if (isAdmin || subject.getId().equals(accountId)) {
      Account account = accountFacade.findById(accountId)
              .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

      if (!CryptHashUtils.verifyVersion(account.getSumOfVersions(), version)) {
        throw new OptimisticLockException();
      }

      if (!account.getAccountType().equals(AccountType.NORMAL)) {
        throw ApplicationExceptionFactory.createInvalidAccountTypeException();
      }

      account.setNewEmail(newEmail);
      accountFacade.update(account);
      String accountChangeEmailToken = tokenService.generateTokenForEmailLink(account, TokenType.CHANGE_EMAIL);
      emailSendingRetryService.sendEmailTokenAfterHalfExpirationTime(account.getLogin(), newEmail,
              TokenType.CHANGE_EMAIL, accountChangeEmailToken);

      mailService.sendEmailWithEmailChangeConfirmLink(newEmail, account.getLocale(), accountChangeEmailToken);

    } else {
      throw ApplicationExceptionFactory.createForbiddenException();
    }
  }

  @PermitAll
  public void changeLocale(Long accountId, String locale) {
    Account account = accountFacade.findById(accountId)
        .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    account.setLocale(locale);
    accountFacade.update(account);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void changeMode(String login, Mode mode) {
    Account account = accountFacade.findByLogin(login)
        .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    account.setMode(mode);
    accountFacade.update(account);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public boolean checkIfUserIsForcedToChangePassword(String login) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    return account.getForcePasswordChange();
  }


  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public String generateTokenFromRefresh(String refreshToken) {
    tokenService.validateRefreshToken(refreshToken);

    String login = tokenService.getLoginFromRefreshToken(refreshToken);
    if (!principal.getName().equals(login)) {
      throw ApplicationExceptionFactory.createForbiddenException();
    }

    Optional<Account> account = accountFacade.findByLogin(login);
    if (account.isEmpty()) {
      throw ApplicationExceptionFactory.createAccountNotFoundException();
    }

    return tokenService.generateToken(account.get());
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<Account> findByFullNameLike(String fullName) {
    return accountFacade.findByFullNameLike(fullName);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Mode getAccountMode(String login) {
    return accountFacade.findByLogin(login).get().getMode();
  }
}
