package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.security.enterprise.AuthenticationException;
import java.time.LocalDateTime;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.MailService;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@Stateless
@DenyAll
public class AuthenticationService {
  @Inject
  private AccountFacadeOperations accountFacade;
  @Inject
  private MailService mailService;
  @Inject
  private TokenService tokenService;

  @PermitAll
  public String login(String login, String password, String ip, String locale) throws AuthenticationException {
    if (password == null) {
      throw ApplicationExceptionFactory.createInvalidCredentialsException();
    }

    Account account = accountFacade
        .findByLogin(login)
        .orElseThrow(ApplicationExceptionFactory::createInvalidCredentialsException);

    validateAccount(account, password, ip);
    account.setFailedLoginCounter(0);
    account.setLastLogin(LocalDateTime.now());
    account.setLastLoginIpAddress(ip);
    if (locale.equals(MessageUtil.LOCALE_PL) || locale.equals(MessageUtil.LOCALE_EN)) {
      account.setLocale(locale);
    }
    accountFacade.update(account);

    return tokenService.generateToken(account);
  }

  @PermitAll
  private void validateAccount(Account account, String password, String ip) throws AuthenticationException {
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
      account.setFailedLoginCounter(account.getFailedLoginCounter() + 1);
      account.setLastFailedLogin(LocalDateTime.now());
      account.setLastFailedLoginIpAddress(ip);
      tryBlockAccount(account);
      throw ApplicationExceptionFactory.createInvalidCredentialsException();
    }
  }

  @PermitAll
  private void tryBlockAccountOperation(Account account)
      throws MessagingException, AccountNotFoundException {
    if (account.getFailedLoginCounter() == 3) {
      account.setAccountState(AccountState.BLOCKED);

      mailService.sendMailWithInfoAboutBlockingAccount(account.getEmail(), account.getLocale());
      account.setFailedLoginCounter(0);
    }
    accountFacade.update(account);
  }

  @PermitAll
  private void tryBlockAccount(Account account) {
    try {
      tryBlockAccountOperation(account);
    } catch (AccountNotFoundException | MessagingException e) {
      //ignore
      // try send mail few times
    }
  }

}
