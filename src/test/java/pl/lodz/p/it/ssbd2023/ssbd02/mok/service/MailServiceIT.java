package pl.lodz.p.it.ssbd2023.ssbd02.mok.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import java.io.File;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.MailServiceOperations;

@ExtendWith(ArquillianExtension.class)
public class MailServiceIT {
  @Inject
  private MailServiceOperations mailService;

  @Resource
  private UserTransaction utx;

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class)
        .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
        .addPackages(true, "org.postgresql")
        .addPackages(true, "org.hamcrest")
        .addPackages(true, "io.jsonwebtoken")
        .addPackages(true, "org.apache")
        .addAsResource(new File("src/test/resources/"), "");
  }

  @Test
  void shouldSendMailWithInfoAboutBlockingAccount() throws SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
      utx.begin();
      assertDoesNotThrow(() -> mailService.sendEmailWithInfoAboutBlockingAccount(
              "jegek60138@fectode.com", "en"));
      utx.commit();
  }

  @Test
  void shouldSendMailWithInfoAboutActivatingAccount() throws SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    assertDoesNotThrow(() -> mailService.sendEmailWithInfoAboutActivatingAccount(
            "jegek60138@fectode.com", "pl"));
    utx.commit();
  }

  @Test
  void shouldSendMailWithInfoAboutConfirmingAccount() throws SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    assertDoesNotThrow(() -> mailService.sendEmailWithInfoAboutConfirmingAccount(
            "jegek60138@fectode.com", "pl"));
    utx.commit();
  }

  @Test
  void shouldSendMailWithAccountConfirmationLink() throws SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
      utx.begin();
      assertDoesNotThrow(() -> mailService.sendEmailWithAccountConfirmationLink(
              "jegek60138@fectode.com", "en", "1", "login"));
      utx.commit();
  }
  @Test
  void shouldSendResetPasswordMail() throws SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
      utx.begin();
      assertDoesNotThrow(() -> mailService.sendResetPasswordEmail(
              "jegek60138@fectode.com", "en", "1"));
      utx.commit();
  }
  @Test
  void shouldSendMailWithEmailChangeConfirmLink() throws SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
      utx.begin();
      assertDoesNotThrow(() -> mailService.sendEmailWithEmailChangeConfirmLink(
              "jegek60138@fectode.com", "en", "token"));
      utx.commit();
  }
  @Test
  void shouldSendMailAboutAddingAccessLevel() throws SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
      utx.begin();
      assertDoesNotThrow(() -> mailService.sendEmailAboutAddingAccessLevel(
              "jegek60138@fectode.com", "en", "TestGroup"));
      utx.commit();
  }
  @Test
  void shouldSendMailAboutRemovingAccessLevel() throws SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
      utx.begin();
      assertDoesNotThrow(() -> mailService.sendEmailAboutRemovingAccessLevel(
              "jegek60138@fectode.com", "en", "TestGroup"));
      utx.commit();
  }

  @Test
  void shouldSendMailAboutChangingAccessLevel() throws SystemException, NotSupportedException,
          HeuristicRollbackException, HeuristicMixedException, RollbackException {
    utx.begin();
    assertDoesNotThrow(() -> mailService.sendEmailAboutChangingAccessLevel(
            "jegek60138@fectode.com", "en", "OldGroup", "NewGroup"));
    utx.commit();
  }
}
