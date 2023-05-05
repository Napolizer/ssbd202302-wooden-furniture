package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.security.enterprise.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountArchiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountBlockedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountIsInactiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.InvalidCredentialsException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.MailService;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@Stateless
public class AuthenticationService {
  @Inject
  private AccountService accountService;
  @Inject
  private MailService mailService;
  @Inject
  private TokenService tokenService;

  public String login(String login, String password) throws AuthenticationException {
    if (password == null) {
      throw ApplicationExceptionFactory.createInvalidCredentialsException();
    }

    Account account = accountService
        .getAccountByLogin(login)
        .orElseThrow(ApplicationExceptionFactory::createInvalidCredentialsException);

    validateAccount(account, password);

    return tokenService.generateToken(account);
  }

  private void validateAccount(Account account, String password) throws AuthenticationException {
    if (account.getArchive()) {
      throw ApplicationExceptionFactory.createAccountArchiveException();
    }

    if (account.getAccountState() == AccountState.NOT_VERIFIED) {
      throw ApplicationExceptionFactory.createAccountNotVerifiedException();
    }

    if (account.getAccountState() == AccountState.BLOCKED) {
      throw ApplicationExceptionFactory.createAccountBlockedException();
    }

    if (account.getAccountState() == AccountState.INACTIVE) {
      throw ApplicationExceptionFactory.createAccountIsInactiveException();
    }

    if (!CryptHashUtils.verifyPassword(password, account.getPassword())) {
      tryBlockAccount(account);
      throw ApplicationExceptionFactory.createInvalidCredentialsException();
    }
  }

  private void tryBlockAccountOperation(Account account)
      throws MessagingException, AccountNotFoundException {
    account.setFailedLoginCounter(account.getFailedLoginCounter() + 1);
    if (account.getFailedLoginCounter() == 3) {
      account.setAccountState(AccountState.BLOCKED);

      mailService.sendMailWithInfoAboutBlockingAccount(account.getEmail(), account.getLocale());
      account.setFailedLoginCounter(0);
    }
    accountService.updateFailedLoginCounter(account);
  }

  private void tryBlockAccount(Account account) {
    try {
      tryBlockAccountOperation(account);
    } catch (AccountNotFoundException | MessagingException e) {
      //ignore
      // try send mail few times
    }
  }

}
