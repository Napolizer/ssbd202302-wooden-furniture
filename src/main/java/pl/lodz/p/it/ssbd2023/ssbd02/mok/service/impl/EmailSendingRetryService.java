package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.mail.MessagingException;
import java.io.InputStream;
import java.util.Properties;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.SimpleLoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.TokenService;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(SimpleLoggerInterceptor.class)
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

  public void sendEmailTokenAfterHalfExpirationTime(String login, String hashOrEmail,
                                                    TokenType tokenType, String token) {
    switch (tokenType) {
      case ACCOUNT_CONFIRMATION -> {
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
      case CHANGE_EMAIL -> timerService.createSingleActionTimer(
              expirationChangeEmail / 2,
              new TimerConfig(new Object[] { login, hashOrEmail, tokenType,  token }, false)
      );
      case PASSWORD_RESET -> timerService.createSingleActionTimer(
              expirationPasswordReset / 2,
              new TimerConfig(new Object[] { login, hashOrEmail, tokenType, token }, false)
      );
      case CHANGE_PASSWORD -> timerService.createSingleActionTimer(
              expirationChangePassword,
              new TimerConfig(new Object[] { login, hashOrEmail, tokenType, token }, false)
      );
      default -> throw new RuntimeException();
    }
  }

  @Timeout
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
          sendMailWithEmailChangeConfirmLink(hashOrEmail, account.getLocale(), token);
        }
      }
      case PASSWORD_RESET -> {
        if (account.getPassword().equals(hashOrEmail)) {
          sendResetPasswordMail(account.getEmail(), account.getLocale(), token);
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

  private void checkTimer(Account account, String token, long time) {
    long timeout = this.expirationAccountConfirmation;
    if (time < timeout) {
      sendAccountConfirmationLink(account, token);
      return;
    }

    if (time == timeout) {
      accountFacade.delete(account);
      sendAccountRemovedMail(account);
    }
  }

  private void sendMailWithEmailChangeConfirmLink(String email, String locale, String token) {
    try {
      mailService.sendMailWithEmailChangeConfirmLink(email, locale, token);
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  private void sendResetPasswordMail(String email, String locale, String token) {
    try {
      mailService.sendResetPasswordMail(email, locale, token);
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  private void sendAccountConfirmationLink(Account account, String token) {
    try {
      mailService.sendMailWithAccountConfirmationLink(account.getEmail(), account.getLocale(),
              token, account.getLogin());
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  private void sendAccountRemovedMail(Account account) {
    try {
      mailService.sendEmailAboutRemovingNotVerifiedAccount(account.getEmail(), account.getLocale());
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

}
