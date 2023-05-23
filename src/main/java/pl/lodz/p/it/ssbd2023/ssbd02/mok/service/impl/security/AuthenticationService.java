package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.security.enterprise.AuthenticationException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import pl.lodz.p.it.ssbd2023.ssbd02.config.Role;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AccountUnblockerServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AuthenticationServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.MailServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.TokenServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountUnblockerService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.MailService;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@Stateless
@DenyAll
public class AuthenticationService implements AuthenticationServiceOperations {
  @Inject
  private AccountFacadeOperations accountFacade;
  @Inject
  private MailServiceOperations mailService;
  @Inject
  private TokenServiceOperations tokenService;
  @Inject
  private AccountUnblockerServiceOperations unblockerService;
  private Long blockadeTimeInSeconds;

  @PostConstruct
  public void init() {
    Properties prop = new Properties();
    try (InputStream input = TokenService.class.getClassLoader().getResourceAsStream("config.properties")) {
      prop.load(input);
      blockadeTimeInSeconds = Long.parseLong(prop.getProperty("account.blockade.time.seconds"));
    } catch (Exception e) {
      long minute = 60;
      long hour = 60 * minute;
      blockadeTimeInSeconds =  24 * hour;
      throw new RuntimeException("Error loading configuration file: " + e.getMessage());
    }
  }

  @PermitAll
  public List<String> login(String login, String password, String ip, String locale) throws AuthenticationException {
    if (password == null) {
      throw ApplicationExceptionFactory.createInvalidCredentialsException();
    }

    Account account = accountFacade
        .findByLogin(login)
        .orElseThrow(ApplicationExceptionFactory::createInvalidCredentialsException);

    if (!account.getAccountType().equals(AccountType.NORMAL)) {
      throw ApplicationExceptionFactory.createInvalidAccountTypeException();
    }

    validateAccount(account, password, ip);
    account.setFailedLoginCounter(0);

    boolean isAdmin = account.getAccessLevels().stream()
            .anyMatch(a -> a.getRoleName().equals(Role.ADMINISTRATOR));

    if (isAdmin) {
      try {
        mailService.sendEmailAboutAdminSession(account.getEmail(), account.getLocale(), ip);
      } catch (MessagingException e) {
        throw ApplicationExceptionFactory.createMailServiceException(e);
      }
    }

    return setUpAccountAndGenerateToken(ip, locale, account);
  }

  @PermitAll
  public List<String> loginWithGoogle(String email, String ip, String locale) {
    Account account = accountFacade
            .findByEmail(email)
            .orElseThrow(ApplicationExceptionFactory::createInvalidLinkException);

    return setUpAccountAndGenerateToken(ip, locale, account);
  }

  @PermitAll
  public List<String> loginWithGithub(String email, String ip, String locale) {
    Account account = accountFacade
        .findByEmail(email)
        .orElseThrow(ApplicationExceptionFactory::createInvalidLinkException);

    return setUpAccountAndGenerateToken(ip, locale, account);
  }

  @PermitAll
  private List<String> setUpAccountAndGenerateToken(String ip, String locale, Account account) {
    account.setLastLogin(LocalDateTime.now());
    account.setLastLoginIpAddress(ip);
    if (locale.equals(MessageUtil.LOCALE_PL) || locale.equals(MessageUtil.LOCALE_EN)) {
      account.setLocale(locale);
    }
    accountFacade.update(account);

    return List.of(tokenService.generateToken(account), tokenService.generateRefreshToken(account));
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
      LocalDateTime blockadeEnd = LocalDateTime.now().plusSeconds(blockadeTimeInSeconds);
      account.setBlockadeEnd(blockadeEnd);
      unblockerService.unblockAccountAfterTimeout(account.getId(), convertToDate(blockadeEnd));
    }
    accountFacade.update(account);
  }

  @PermitAll
  private void tryBlockAccount(Account account) {
    try {
      tryBlockAccountOperation(account);
    } catch (AccountNotFoundException | MessagingException e) {
      //ignore
    }
  }

  private Date convertToDate(LocalDateTime localDateTime) {
    Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    return Date.from(instant);
  }

}
