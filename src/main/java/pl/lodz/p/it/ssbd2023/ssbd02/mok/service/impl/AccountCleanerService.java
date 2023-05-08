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
import jakarta.mail.MessagingException;
import java.io.InputStream;
import java.util.Properties;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;


@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountCleanerService {
  private long timeout;
  @Resource
  private TimerService timerService;
  @Inject
  private MailService mailService;
  @Inject
  private AccountFacadeOperations accountFacade;

  @PostConstruct
  public void init() {
    int hour = 60 * 60 * 1000;
    try {
      Properties properties = new Properties();
      InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties");
      properties.load(is);
      long numberOfHours = Long.parseLong(properties.getProperty("delete.account.time.hours"));
      this.timeout = numberOfHours * hour;
      //this.timeout = numberOfHours * 1000;

    } catch (Exception e) {
      this.timeout = 24 * hour;
    }
  }


  public void deleteAccountAfterTimeout(String login) {
    timerService.createSingleActionTimer(
            timeout / 2,
            new TimerConfig(new Object[] { login, timeout / 2 }, false)
    );

    timerService.createSingleActionTimer(
            timeout,
            new TimerConfig(new Object[] { login, timeout }, false)
    );
  }

  @Timeout
  public void deleteAccount(Timer timer) {
    Object[] info = (Object[]) timer.getInfo();
    String login = (String) info[0];
    long time = (long) info[1];
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(AccountNotFoundException::new);

    if (account.getAccountState().equals(AccountState.NOT_VERIFIED)) {
      checkTimer(account, time);
    }
  }

  private void checkTimer(Account account, long time) {
    if (time < timeout) {
      sendReminderMail(account);
      return;
    }

    if (time == timeout) {
      accountFacade.delete(account);
      sendAccountRemovedMail(account);
    }
  }

  private void sendReminderMail(Account account) {
    try {
      mailService.sendEmailRemindingToConfirmAccount(account.getEmail(), account.getLocale());
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
