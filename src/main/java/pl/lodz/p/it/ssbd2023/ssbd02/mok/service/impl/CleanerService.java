package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.io.InputStream;
import java.util.Properties;

import jakarta.mail.MessagingException;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;


@Stateless
public class CleanerService {
  private long timeout;
  @Resource
  private TimerService timerService;

  @Inject
  private MailService mailService;

  @PostConstruct
  public void init() {
    int hour = 60 * 60 * 1000;
    try {
      Properties properties = new Properties();
      InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties");
      properties.load(is);
      long numberOfHours = Long.parseLong(properties.getProperty("delete.account.time.hours"));
//      this.timeout = numberOfHours * hour;
      this.timeout = numberOfHours * 1000; //For tests

    } catch (Exception e) {
      this.timeout = 24 * hour;
    }

  }
  @Inject
  private AccountFacadeOperations accountFacade;

  public void deleteAccountAfterTimeout(Account account) {
    Timer timer = timerService.createSingleActionTimer(
            timeout,
            new TimerConfig(account, false)
    );
  }

  @Timeout
  public void deleteAccount(Timer timer) {
    Account account = (Account) timer.getInfo();
    if (account.getAccountState().equals(AccountState.NOT_VERIFIED)) {
      String email = account.getEmail();
      String locale = account.getLocale();

      accountFacade.delete(account);
      try {
        mailService.sendEmailAboutRemovingNotVerifiedAccount(email, locale);
      } catch (MessagingException e) {
        throw ApplicationExceptionFactory.createMailServiceException(e);
      }
    }
  }

}
