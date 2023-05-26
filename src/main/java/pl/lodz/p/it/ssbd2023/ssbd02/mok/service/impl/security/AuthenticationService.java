package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.mail.MessagingException;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import pl.lodz.p.it.ssbd2023.ssbd02.config.Role;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AccountUnblockerServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AuthenticationServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.MailServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.TokenServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({ LoggerInterceptor.class })
@DenyAll
public class AuthenticationService extends AbstractService implements AuthenticationServiceOperations {
  @Inject
  private AccountFacadeOperations accountFacade;
  @Inject
  private MailServiceOperations mailService;
  @Inject
  private TokenServiceOperations tokenService;
  @Inject
  private AccountUnblockerServiceOperations unblockerService;
  @Inject
  private HttpServletRequest servletRequest;
  private Long blockadeTimeInSeconds;
  private Integer failedAccountAuthenticationAttempts;

  @PostConstruct
  public void init() {
    Properties prop = new Properties();
    try (InputStream input = TokenService.class.getClassLoader().getResourceAsStream("config.properties")) {
      prop.load(input);
      blockadeTimeInSeconds = Long.parseLong(prop.getProperty("account.blockade.time.seconds"));
      failedAccountAuthenticationAttempts =
              Integer.parseInt(prop.getProperty("account.failed.authentication.attempts"));
    } catch (Exception e) {
      long minute = 60;
      long hour = 60 * minute;
      blockadeTimeInSeconds =  24 * hour;
      failedAccountAuthenticationAttempts = 3;
    }
  }

  @PermitAll
  public List<String> login(String login, String password, String locale) throws AuthenticationException {
    if (password == null) {
      throw ApplicationExceptionFactory.createInvalidCredentialsException();
    }

    Account account = accountFacade
        .findByLogin(login)
        .orElseThrow(ApplicationExceptionFactory::createInvalidCredentialsException);

    if (!account.getAccountType().equals(AccountType.NORMAL)) {
      throw ApplicationExceptionFactory.createInvalidAccountTypeException();
    }

    validateAccount(account, password);
    account.setFailedLoginCounter(0);

    boolean isAdmin = account.getAccessLevels().stream()
            .anyMatch(a -> a.getRoleName().equals(Role.ADMINISTRATOR));

    if (isAdmin) {
      mailService.sendEmailAboutAdminSession(account.getEmail(), account.getLocale(), getRemoteAddress());

    }

    return setUpAccountAndGenerateToken(locale, account);
  }

  @PermitAll
  public List<String> loginWithGoogle(String email, String locale) {
    Account account = accountFacade
            .findByEmail(email)
            .orElseThrow(ApplicationExceptionFactory::createInvalidLinkException);

    return setUpAccountAndGenerateToken(locale, account);
  }

  @PermitAll
  public List<String> loginWithGithub(String email, String locale) {
    Account account = accountFacade
        .findByEmail(email)
        .orElseThrow(ApplicationExceptionFactory::createInvalidLinkException);

    return setUpAccountAndGenerateToken(locale, account);
  }

  @PermitAll
  private List<String> setUpAccountAndGenerateToken(String locale, Account account) {
    account.setLastLogin(LocalDateTime.now());
    account.setLastLoginIpAddress(getRemoteAddress());
    if (locale.equals(MessageUtil.LOCALE_PL) || locale.equals(MessageUtil.LOCALE_EN)) {
      account.setLocale(locale);
    }
    accountFacade.update(account);

    return List.of(tokenService.generateToken(account), tokenService.generateRefreshToken(account));
  }

  @PermitAll
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
      account.setFailedLoginCounter(account.getFailedLoginCounter() + 1);
      account.setLastFailedLogin(LocalDateTime.now());
      account.setLastFailedLoginIpAddress(getRemoteAddress());
      tryBlockAccount(account);
      throw ApplicationExceptionFactory.createInvalidCredentialsException();
    }
  }

  @PermitAll
  private void tryBlockAccountOperation(Account account)
      throws MessagingException, AccountNotFoundException {

    if (Objects.equals(account.getFailedLoginCounter(), failedAccountAuthenticationAttempts)) {
      account.setAccountState(AccountState.BLOCKED);

      mailService.sendEmailWithInfoAboutBlockingAccount(account.getEmail(), account.getLocale());
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

  @PermitAll
  private Date convertToDate(LocalDateTime localDateTime) {
    Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    return Date.from(instant);
  }

  private String getRemoteAddress() {
    String forwarded = servletRequest.getHeader("X-FORWARDED-FOR");
    if (forwarded != null) {
      return forwarded.contains(",") ? forwarded.split(",")[0] : forwarded;
    }
    return servletRequest.getRemoteAddr();
  }

}
