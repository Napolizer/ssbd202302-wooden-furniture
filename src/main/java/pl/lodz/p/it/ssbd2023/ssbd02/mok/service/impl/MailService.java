package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

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
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.MailServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({LoggerInterceptor.class})
@DenyAll
public class MailService extends AbstractService implements MailServiceOperations {
  @Inject
  private EnvironmentConfig environmentConfig;

  private final String appUrl = "http://localhost:4200";

  @Override
  @PermitAll
  public void sendEmailWithInfoAboutBlockingAccount(String to, String locale) {
    try {
      sendEmail(to,
          MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_BLOCKED_SUBJECT),
          MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_BLOCKED_MESSAGE)
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailWithInfoAboutActivatingAccount(String to, String locale) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACTIVATED_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACTIVATED_MESSAGE)
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailWithInfoAboutConfirmingAccount(String to, String locale) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_VERIFIED_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_VERIFIED_MESSAGE)
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailWithAccountConfirmationLink(String to, String locale, String token, String login) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CONFIRMATION_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CONFIRMATION_TOPIC1)
                      + (" " + login + ",\n")
                      + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CONFIRMATION_TOPIC2)
                      + ("\n" + appUrl + "/confirm?token=" + token)
                      + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CONFIRMATION_TOPIC3));
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendResetPasswordEmail(String to, String locale, String resetPasswordToken) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_RESET_PASSWORD_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_RESET_PASSWORD_MESSAGE1)
              + " " + appUrl + "/reset-password?token=" + resetPasswordToken + " "
              + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_RESET_PASSWORD_MESSAGE2));
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailWithEmailChangeConfirmLink(String to, String locale, String token) {
    try {
      sendEmail(to,
          MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_EMAIL_CHANGE_SUBJECT),
          MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_EMAIL_CHANGE_TOPIC)
          + ("\n" + appUrl + "/change-email/confirm?token=" + token)
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailAboutAddingAccessLevel(String to, String locale, String groupName) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_ADDED_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_ADDED_MESSAGE1)
              + " " + groupName
              + " " + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_ADDED_MESSAGE2)
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailWithPasswordChangeLink(String to, String locale, String token) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CHANGE_PASSWORD_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CHANGE_PASSWORD_TOPIC)
                      + ("\n" + appUrl + "/change-password/confirm?token=" + token)
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailAboutRemovingAccessLevel(String to, String locale, String groupName) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_MESSAGE1)
              + " " + groupName
              + " " + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_MESSAGE2)
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailAboutChangingAccessLevel(String to, String locale, String oldGroup, String newGroup) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_CHANGED_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_CHANGED_MESSAGE1)
              + " " + oldGroup
              + " " + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_CHANGED_MESSAGE2)
              + " " + newGroup + "."
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailAboutRemovingNotVerifiedAccount(String to, String locale) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_REMOVED_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_REMOVED_MESSAGE)
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailAboutAdminSession(String to, String locale, String ip) {
    try {
      sendEmail(to,
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ADMIN_LOGIN_SESSION_SUBJECT),
              MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ADMIN_LOGIN_SESSION_MESSAGE)
              + ip + "."
      );
    } catch (MessagingException e) {
      throw ApplicationExceptionFactory.createMailServiceException(e);
    }
  }

  @Override
  @PermitAll
  public void sendEmailAboutChangingOrderState(String to, String locale) {
    try {
      sendEmail(to,
                MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ORDER_STATE_CHANGE_SUBJECT),
                MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ORDER_STATE_CHANGE_MESSAGE)
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
