package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import java.io.InputStream;
import java.util.Properties;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.config.enums.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.SimpleLoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.TokenService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(SimpleLoggerInterceptor.class)
@DenyAll
public class EmailSendingRetryService {
  private Long expirationAccountConfirmation;
  private Long expirationPasswordReset;
  private Long expirationChangeEmail;
  private Long expirationChangePassword;
  @Resource
  private TimerService timerService;
  @Inject
  private MailService mailService;
  @Inject
  private AccountService accountService;
  @Inject
  private AccountFacadeOperations accountFacade;

  @PostConstruct
  public void init() {
    Properties prop = new Properties();
    try (InputStream input = TokenService.class.getClassLoader().getResourceAsStream("config.properties")) {
      prop.load(input);
      expirationAccountConfirmation = Long.parseLong(prop.getProperty("expiration.account.confirmation.milliseconds"));
      expirationPasswordReset = Long.parseLong(prop.getProperty("expiration.password.reset.milliseconds"));
      expirationChangeEmail = Long.parseLong(prop.getProperty("expiration.change.email.milliseconds"));
      expirationChangePassword = Long.parseLong(prop.getProperty("expiration.password.change.milliseconds"));
    } catch (Exception e) {
      throw new RuntimeException("Error loading configuration file: " + e.getMessage());
    }
  }

  @PermitAll
  public void sendEmailTokenAfterHalfExpirationTime(String login, String hashOrEmail,
                                                    TokenType tokenType, String token) {
    switch (tokenType) {
      case ACCOUNT_CONFIRMATION -> createAccountConfirmationTimer(login, hashOrEmail, tokenType, token);
      case CHANGE_EMAIL -> createChangeEmailTimer(login, hashOrEmail, tokenType, token);
      case PASSWORD_RESET -> createResetPasswordTimer(login, hashOrEmail, tokenType, token);
      case CHANGE_PASSWORD -> createChangePasswordTimer(login, hashOrEmail, tokenType, token);
      default -> throw new RuntimeException();
    }
  }

  @PermitAll
  private void createAccountConfirmationTimer(String login, String hashOrEmail, TokenType tokenType, String token) {
    timerService.createSingleActionTimer(
            expirationAccountConfirmation / 2,
            new TimerConfig(new Object[] { login, hashOrEmail, tokenType, token,
                    expirationAccountConfirmation / 2 }, false)
    );
    timerService.createSingleActionTimer(
            expirationAccountConfirmation,
            new TimerConfig(new Object[] { login, hashOrEmail, tokenType, token,
                    expirationAccountConfirmation }, false)
    );
  }

  @PermitAll
  private void createChangeEmailTimer(String login, String hashOrEmail, TokenType tokenType, String token) {
    timerService.createSingleActionTimer(
            expirationChangeEmail / 2,
            new TimerConfig(new Object[] { login, hashOrEmail, tokenType,  token }, false)
    );
  }

  @PermitAll
  private void createResetPasswordTimer(String login, String hashOrEmail, TokenType tokenType, String token) {
    timerService.createSingleActionTimer(
            expirationPasswordReset / 2,
            new TimerConfig(new Object[] { login, hashOrEmail, tokenType, token }, false)
    );
  }

  @PermitAll
  private void createChangePasswordTimer(String login, String hashOrEmail, TokenType tokenType, String token) {
    timerService.createSingleActionTimer(
            expirationChangePassword,
            new TimerConfig(new Object[] { login, hashOrEmail, tokenType, token }, false)
    );
  }

  @Timeout
  @PermitAll
  public void send(Timer timer) {
    Object[] info = (Object[]) timer.getInfo();
    String login = (String) info[0];
    String hashOrEmail = (String) info[1];
    TokenType tokenType = (TokenType) info[2];
    String token = (String) info[3];
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(AccountNotFoundException::new);

    switch (tokenType) {
      case CHANGE_EMAIL -> {
        if (!account.getEmail().equals(hashOrEmail)) {
          mailService.sendEmailWithEmailChangeConfirmLink(hashOrEmail, account.getLocale(), token);
        }
      }
      case PASSWORD_RESET -> {
        if (account.getPassword().equals(hashOrEmail)) {
          mailService.sendResetPasswordEmail(account.getEmail(), account.getLocale(), token);
        }
      }
      case ACCOUNT_CONFIRMATION -> {
        Long time = (Long) info[4];
        if (account.getAccountState().equals(AccountState.NOT_VERIFIED)) {
          checkTimer(account, token, time);
        }
      }
      case CHANGE_PASSWORD -> {
        if (hashOrEmail.equals(account.getPassword())) {
          accountService.blockAccount(account.getId());
        }
      }
      default -> throw new RuntimeException();
    }
  }

  @PermitAll
  private void checkTimer(Account account, String token, long time) {
    long timeout = this.expirationAccountConfirmation;
    if (time < timeout) {
      mailService.sendEmailWithAccountConfirmationLink(account.getEmail(), account.getLocale(),
              token, account.getLogin());
      return;
    }

    if (time == timeout) {
      accountFacade.delete(account);
      mailService.sendEmailAboutRemovingNotVerifiedAccount(account.getEmail(), account.getLocale());
    }
  }


}
