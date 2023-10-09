package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import pl.lodz.p.it.ssbd2023.ssbd02.config.EnvironmentConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.MailServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({LoggerInterceptor.class})
@DenyAll
public class MozMailService extends AbstractService implements MailServiceOperations {
  @Inject
  private EnvironmentConfig environmentConfig;

  @Override
  @PermitAll
  public void sendEmailAboutOrderStateChange(String to, String locale, String orderedProducts, String orderOldState,
                                             String orderNewState) {
    try {
      sendEmail(to,
          MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ORDER_STATE_CHANGE_SUBJECT),
          MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ORDER_STATE_CHANGE_MESSAGE1)
              + "\n" + orderedProducts
              + "\n" + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ORDER_STATE_CHANGE_MESSAGE2)
              + " " + MessageUtil.getMessage(locale, orderOldState) + " " + MessageUtil.getMessage(locale,
              MessageUtil.MessageKey.EMAIL_ORDER_STATE_CHANGE_MESSAGE3) + " "
                  + MessageUtil.getMessage(locale, orderNewState) + "."
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @PermitAll
  public void sendEmail(String to, String subject, String message) throws MessagingException {
    if (environmentConfig.isTest()) {
      return;
    }

    Session session = getSession();
    MimeMessage mail = new MimeMessage(session);
    mail.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    mail.setSubject(subject);
    mail.setText(message);
    Transport.send(mail);
  }

  @PermitAll
  private Properties getMailProperties() {
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.auth", true);
    props.put("mail.smtp.port", "465");

    return props;
  }

  @PermitAll
  private Session getSession() {
    return Session.getInstance(getMailProperties(), new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(
            //requires variables in os
            System.getenv("MAIL_MAIL"),
            System.getenv("MAIL_PASSWORD")
        );
      }
    });
  }
}
