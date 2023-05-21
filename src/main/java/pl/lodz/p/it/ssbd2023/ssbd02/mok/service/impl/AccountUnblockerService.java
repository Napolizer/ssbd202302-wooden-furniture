package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

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
import jakarta.mail.MessagingException;
import java.util.Date;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.SimpleLoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;


@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(SimpleLoggerInterceptor.class)
@DenyAll
public class AccountUnblockerService {
  @Inject
  private AccountFacadeOperations accountFacade;
  @Inject
  private MailService mailService;
  @Resource
  private TimerService timerService;

  @PermitAll
  public void unblockAccountAfterTimeout(Long accountId, Date operationTime) {
    timerService.createSingleActionTimer(
            operationTime,
            new TimerConfig(new Object[] { accountId }, false)
    );
  }

  @Timeout
  @PermitAll
  public void send(Timer timer) {
    Object[] info = (Object[]) timer.getInfo();
    Long accountId = (Long) info[0];

    Account account = accountFacade.findById(accountId)
            .orElseThrow(AccountNotFoundException::new);

    if (account.getAccountState().equals(AccountState.BLOCKED)) {
      account.setAccountState(AccountState.ACTIVE);
      account.setBlockadeEnd(null);
      accountFacade.update(account);

      mailService.sendMailWithInfoAboutActivatingAccount(account.getEmail(),
              account.getLocale());

    }
  }
}
