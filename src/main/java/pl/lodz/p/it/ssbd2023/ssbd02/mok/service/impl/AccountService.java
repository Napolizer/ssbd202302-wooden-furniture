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
import jakarta.mail.MessagingException;
import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.config.Role;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountType;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.TokenService;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.service.AbstractService;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericServiceExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class AccountService extends AbstractService {
  @Inject
  private AccountFacadeOperations accountFacade;
  @Inject
  private MailService mailService;
  @Inject
  private TokenService tokenService;
  @Inject
  private EmailSendingRetryService emailSendingRetryService;

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
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
  public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) {
    Account foundAccount =
        accountFacade.findById(accountId).orElseThrow(AccountNotFoundException::new);
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
    accountFacade.update(foundAccount);
    try {
      mailService.sendEmailAboutAddingAccessLevel(foundAccount.getEmail(),
              foundAccount.getLocale(), accessLevel.getRoleName());
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @RolesAllowed(ADMINISTRATOR)
  public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) {
    Account foundAccount =
        accountFacade.findById(accountId).orElseThrow(AccountNotFoundException::new);
    List<AccessLevel> accessLevels = foundAccount.getAccessLevels();

    if (accessLevels.size() <= 1) {
      throw ApplicationExceptionFactory.createRemoveAccessLevelException();
    }

    for (AccessLevel item : accessLevels) {
      if (item.getClass() == accessLevel.getClass()) {
        accessLevels.remove(item);
        foundAccount.setAccessLevels(accessLevels);
        accountFacade.update(foundAccount);
        try {
          mailService.sendEmailAboutRemovingAccessLevel(foundAccount.getEmail(),
                  foundAccount.getLocale(), item.getRoleName());
        } catch (MessagingException e) {
          throw ApplicationExceptionFactory.createMailServiceException(e);
        }
        return;
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
      accessLevels.set(0, accessLevel);
      accessLevel.setAccount(account);
      account.setAccessLevels(accessLevels);
      accountFacade.update(account);
      try {
        mailService.sendEmailAboutChangingAccessLevel(account.getEmail(),
                account.getLocale(), oldAccessLevel.getRoleName(), accessLevel.getRoleName());
      } catch (MessagingException e) {
        throw ApplicationExceptionFactory.createMailServiceException(e);
      }
      return account;
    }
    throw ApplicationExceptionFactory.createMoreThanOneAccessLevelAssignedException();
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void editAccountInfo(String login, Account accountWithChanges, String hash) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

    if (!CryptHashUtils.verifyVersion(account.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    account.update(accountWithChanges);
    accountFacade.update(account);
  }

  @RolesAllowed(ADMINISTRATOR)
  public void editAccountInfoAsAdmin(String login, Account accountWithChanges, String hash) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

    if (!CryptHashUtils.verifyVersion(account.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    account.update(accountWithChanges);
    accountFacade.update(account);
  }

  @PermitAll
  public void registerAccount(Account account) {
    account.setAccountState(AccountState.NOT_VERIFIED);
    account.setFailedLoginCounter(0);
    account.setPassword(CryptHashUtils.hashPassword(account.getPassword()));
    account.setAccountType(AccountType.NORMAL);
    Account persistedAccount = accountFacade.create(account);
    String accountConfirmationToken = tokenService.generateTokenForEmailLink(account, TokenType.ACCOUNT_CONFIRMATION);
    emailSendingRetryService.sendEmailTokenAfterHalfExpirationTime(account.getLogin(), null,
            TokenType.ACCOUNT_CONFIRMATION, accountConfirmationToken);
    try {
      mailService.sendMailWithAccountConfirmationLink(account.getEmail(),
              account.getLocale(), accountConfirmationToken, account.getLogin());
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @PermitAll
  public void registerGoogleAccount(Account account) {
    account.setAccountState(AccountState.ACTIVE);
    account.setPassword(CryptHashUtils.hashPassword(account.getPassword()));
    account.setAccountType(AccountType.GOOGLE);
    accountFacade.create(account);
  }

  @PermitAll
  public void registerGithubAccount(Account account) {
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
    return accountFacade.create(account);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Account changePassword(String login, String newPassword, String currentPassword) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

    if (!account.getAccountType().equals(AccountType.NORMAL)) {
      throw ApplicationExceptionFactory.createInvalidAccountTypeException();
    }

    if (!CryptHashUtils.verifyPassword(currentPassword, account.getPassword())) {
      throw ApplicationExceptionFactory.createAccountNotVerifiedException();
      //fixme invalidCredentialsException is checked
    }

    if (!CryptHashUtils.verifyPassword(newPassword, account.getPassword())) {
      account.setPassword(CryptHashUtils.hashPassword(newPassword));
      return accountFacade.update(account);
    } else {
      throw ApplicationExceptionFactory.createOldPasswordGivenException();
    }
  }

  @PermitAll
  public Account changePasswordFromLink(String token, String newPassword, String currentPassword) {
    String login = tokenService.getLoginFromTokenWithoutValidating(token, TokenType.CHANGE_PASSWORD);

    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

    if (!CryptHashUtils.verifyPassword(currentPassword, account.getPassword())) {
      throw ApplicationExceptionFactory.createAccountNotVerifiedException();
      //fixme invalidCredentialsException is checked
    }

    if (!CryptHashUtils.verifyPassword(newPassword, account.getPassword())) {
      account.setPassword(CryptHashUtils.hashPassword(newPassword));
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
    try {
      mailService.sendMailWithPasswordChangeLink(account.getEmail(), account.getLocale(), changePasswordToken);
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @PermitAll
  public void blockAccount(Long id) {
    Account account = accountFacade.findById(id).orElseThrow(AccountNotFoundException::new);

    if (!account.getAccountState().equals(AccountState.ACTIVE)) {
      throw ApplicationExceptionFactory.createIllegalAccountStateChangeException();
    }

    account.setAccountState(AccountState.BLOCKED);
    accountFacade.update(account);

    try {
      mailService.sendMailWithInfoAboutBlockingAccount(account.getEmail(),
              account.getLocale());
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @RolesAllowed(ADMINISTRATOR)
  public void activateAccount(Long id) {
    Account account = accountFacade.findById(id).orElseThrow(AccountNotFoundException::new);
    AccountState state = account.getAccountState();

    if (state.equals(AccountState.INACTIVE) || state.equals(AccountState.ACTIVE)) {
      throw ApplicationExceptionFactory.createIllegalAccountStateChangeException();
    }

    account.setAccountState(AccountState.ACTIVE);
    accountFacade.update(account);

    try {
      mailService.sendMailWithInfoAboutActivatingAccount(account.getEmail(),
              account.getLocale());
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
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
    try {
      mailService.sendMailWithInfoAboutConfirmingAccount(account.getEmail(),
              account.getLocale());
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
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
  public void resetPassword(String login, String password) {
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createPasswordResetExpiredLinkException);
    String hash = CryptHashUtils.hashPassword(password);
    if (CryptHashUtils.verifyPassword(password, account.getPassword())) {
      throw ApplicationExceptionFactory.createOldPasswordGivenException();
    }
    account.setPassword(hash);
    accountFacade.update(account);
  }

  @PermitAll
  public void sendResetPasswordEmail(String email) {
    Account account = getAccountByEmail(email).get();
    if (!account.getAccountType().equals(AccountType.NORMAL)) {
      throw ApplicationExceptionFactory.createInvalidAccountTypeException();
    }
    String resetPasswordToken = tokenService.generateTokenForEmailLink(account, TokenType.PASSWORD_RESET);
    emailSendingRetryService.sendEmailTokenAfterHalfExpirationTime(account.getLogin(), account.getPassword(),
            TokenType.PASSWORD_RESET, resetPasswordToken);
    try {
      mailService.sendResetPasswordMail(account.getEmail(),
              account.getLocale(), resetPasswordToken);
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Account updateEmailAfterConfirmation(String login) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    account.setEmail(account.getNewEmail());
    account.setNewEmail(null);
    return accountFacade.update(account);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void changeEmail(String newEmail, Long accountId, String principal) {
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

      if (!account.getAccountType().equals(AccountType.NORMAL)) {
        throw ApplicationExceptionFactory.createInvalidAccountTypeException();
      }

      account.setNewEmail(newEmail);
      accountFacade.update(account);
      String accountChangeEmailToken = tokenService.generateTokenForEmailLink(account, TokenType.CHANGE_EMAIL);
      emailSendingRetryService.sendEmailTokenAfterHalfExpirationTime(account.getLogin(), newEmail,
              TokenType.CHANGE_EMAIL, accountChangeEmailToken);
      try {
        mailService.sendMailWithEmailChangeConfirmLink(newEmail,
                account.getLocale(), accountChangeEmailToken);
      } catch (MessagingException e) {
        throw ApplicationExceptionFactory.createMailServiceException(e);
      }
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
}
