package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
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
public class AccountService extends AbstractService {
  @Inject
  private AccountFacadeOperations accountFacade;
  @Inject
  private MailService mailService;
  @Inject
  private TokenService tokenService;
  @Inject
  private EmailSendingRetryService emailSendingRetryService;

  public Optional<Account> getAccountByLogin(String login) {
    return accountFacade.findByLogin(login);
  }

  public Optional<Account> getAccountById(Long id) {
    return accountFacade.findById(id);
  }

  public Optional<Account> getAccountByEmail(String email) {
    return accountFacade.findByEmail(email);
  }

  public List<Account> getAccountList() {
    return accountFacade.findAll();
  }

  public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) {
    Account foundAccount =
        accountFacade.findById(accountId).orElseThrow(AccountNotFoundException::new);
    List<AccessLevel> accessLevels = foundAccount.getAccessLevels();

    if ((accessLevels.size() > 0 && Objects.equals(accessLevel.getGroupName(), "ADMINISTRATOR"))) {
      throw ApplicationExceptionFactory.createAdministratorAccessLevelAlreadyAssignedException();
    }

    for (AccessLevel item : accessLevels) {
      if (Objects.equals(item.getGroupName(), "ADMINISTRATOR")) {
        throw ApplicationExceptionFactory.createAdministratorAccessLevelAlreadyAssignedException();
      }

      if (Objects.equals(item.getGroupName(), accessLevel.getGroupName())) {
        throw ApplicationExceptionFactory.createAccessLevelAlreadyAssignedException();
      }

      if ((Objects.equals(item.getGroupName(), "CLIENT") && Objects.equals(accessLevel.getGroupName(), "SALES_REP"))
          || (Objects.equals(item.getGroupName(), "SALES_REP")
            && Objects.equals(accessLevel.getGroupName(), "CLIENT"))) {
        throw ApplicationExceptionFactory.createClientAndSalesRepAccessLevelsConflictException();
      }
    }
    accessLevel.setAccount(foundAccount);
    accessLevels.add(accessLevel);
    foundAccount.setAccessLevels(accessLevels);
    accountFacade.update(foundAccount);
  }

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
        return;
      }
    }
    throw ApplicationExceptionFactory.createAccessLevelNotAssignedException();
  }

  public Account changeAccessLevel(Long accountId, AccessLevel accessLevel) {
    Account account = accountFacade.findById(accountId).orElseThrow(AccountNotFoundException::new);
    List<AccessLevel> accessLevels = account.getAccessLevels();

    if (accessLevels.size() == 1) {
      accessLevels.set(0, accessLevel);
      accessLevel.setAccount(account);
      account.setAccessLevels(accessLevels);
      accountFacade.update(account);
      return account;
    }
    throw ApplicationExceptionFactory.createMoreThanOneAccessLevelAssignedException();
  }

  public void editAccountInfo(String login, Account accountWithChanges) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    account.update(accountWithChanges);
    accountFacade.update(account);
  }

  public void editAccountInfoAsAdmin(String login, Account accountWithChanges) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    account.update(accountWithChanges);
    accountFacade.update(account);
  }

  public void registerAccount(Account account) {
    account.setAccountState(AccountState.NOT_VERIFIED);
    account.setFailedLoginCounter(0);
    account.setPassword(CryptHashUtils.hashPassword(account.getPassword()));
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

  public void createAccount(Account account) {
    account.setFailedLoginCounter(0);
    account.setPassword(CryptHashUtils.hashPassword(account.getPassword()));
    accountFacade.create(account);
  }

  public void changePassword(String login, String newPassword) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

    if (!Objects.equals(account.getPassword(), newPassword)) {
      account.setPassword(newPassword);
      accountFacade.update(account);
    }
  }

  public void changePasswordAsAdmin(String login, String newPassword) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);

    if (!Objects.equals(account.getPassword(), newPassword)) {
      account.setPassword(newPassword);
      accountFacade.update(account);
    }
  }

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

  public void activateAccount(Long id) {
    Account account = accountFacade.findById(id).orElseThrow(AccountNotFoundException::new);
    AccountState state = account.getAccountState();

    if (!state.equals(AccountState.BLOCKED)) {
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

  public void updateFailedLoginCounter(Account account) {
    Account found =
        accountFacade.findById(account.getId()).orElseThrow(AccountNotFoundException::new);
    found.setFailedLoginCounter(account.getFailedLoginCounter());
    found.setAccountState(account.getAccountState());

    accountFacade.update(found);
  }

  public void confirmAccount(String token) {
    String login = tokenService.validateEmailToken(token, TokenType.ACCOUNT_CONFIRMATION, null);
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountConfirmationExpiredLinkException);
    if (!account.getAccountState().equals(AccountState.NOT_VERIFIED)) {
      throw ApplicationExceptionFactory.createAccountAlreadyVerifiedException();
    }
    account.setAccountState(AccountState.ACTIVE);
    Client client = new Client();
    client.setAccount(account);
    account.getAccessLevels().add(client);
  }

  public String validatePasswordResetToken(String token) {
    String login = tokenService.getLoginFromTokenWithoutValidating(token, TokenType.PASSWORD_RESET);
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createPasswordResetExpiredLinkException);
    tokenService.validateEmailToken(token, TokenType.PASSWORD_RESET, account.getPassword());
    return login;
  }

  public String validateChangeEmailToken(String token) {
    String login = tokenService.getLoginFromTokenWithoutValidating(token, TokenType.CHANGE_EMAIL);
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createChangeEmailExpiredLinkException);
    tokenService.validateEmailToken(token, TokenType.CHANGE_EMAIL, account.getNewEmail());
    return login;
  }

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

  public void sendResetPasswordEmail(String email) {
    Account account = getAccountByEmail(email).get();
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

  public Account updateEmailAfterConfirmation(String login) {
    Account account = accountFacade.findByLogin(login).orElseThrow(AccountNotFoundException::new);
    account.setEmail(account.getNewEmail());
    account.setNewEmail(null);
    return accountFacade.update(account);
  }

  public void changeEmail(String newEmail, Long accountId, String principal) {
    Account subject = accountFacade.findByLogin(principal)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    if (accountFacade.findByEmail(newEmail).isPresent()) {
      throw ApplicationExceptionFactory.createEmailAlreadyExistsException();
    }

    boolean isAdmin = subject.getAccessLevels()
            .stream().anyMatch(al -> al.getGroupName().equals("ADMINISTRATOR"));

    if (isAdmin || subject.getId().equals(accountId)) {
      Account account = accountFacade.findById(accountId)
              .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
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
}
