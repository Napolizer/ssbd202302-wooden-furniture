package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
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
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({LoggerInterceptor.class})
public class MailService {
  private final String appUrl = "http://localhost:4200";

  public void sendMailWithInfoAboutBlockingAccount(String to, String locale)
      throws MessagingException {
    sendMail(to,
        MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_BLOCKED_SUBJECT),
        MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_BLOCKED_MESSAGE)
    );
  }

  public void sendMailWithInfoAboutActivatingAccount(String to, String locale)
          throws MessagingException {
    sendMail(to,
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACTIVATED_SUBJECT),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACTIVATED_MESSAGE)
    );
  }

  public void sendMailWithAccountConfirmationLink(String to, String locale, String token, String login)
          throws MessagingException {
    sendMail(to,
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CONFIRMATION_SUBJECT),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CONFIRMATION_TOPIC1)
                    + (" " + login + ",\n")
                    + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CONFIRMATION_TOPIC2)
                    + ("\n" + appUrl + "/confirm?token=" + token)
                    + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_CONFIRMATION_TOPIC3));
  }

  public void sendResetPasswordMail(String to, String locale, String resetPasswordToken) throws MessagingException {
    sendMail(to,
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_RESET_PASSWORD_SUBJECT),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_RESET_PASSWORD_MESSAGE1)
            + " " + appUrl + "/reset-password?token=" + resetPasswordToken + " "
            + MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_RESET_PASSWORD_MESSAGE2));
  }

  public void sendMailWithEmailChangeConfirmLink(String to, String locale, String token)
      throws MessagingException {
    sendMail(to,
        MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_EMAIL_CHANGE_SUBJECT),
        MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_EMAIL_CHANGE_TOPIC)
        + ("\n" + appUrl + "/change-email/confirm?token=" + token)
    );
  }

  public void sendEmailAboutAddingAccessLevel(String to, String locale) throws MessagingException {
    sendMail(to,
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_ADDED_SUBJECT),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_ADDED_MESSAGE)
    );
  }

  public void sendEmailAboutRemovingAccessLevel(String to, String locale) throws MessagingException {
    sendMail(to,
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_SUBJECT),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_MESSAGE)
    );
  }

  public void sendEmailAboutRemovingNotVerifiedAccount(String to, String locale) throws MessagingException {
    sendMail(to,
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_REMOVED_SUBJECT),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_REMOVED_MESSAGE)
    );
  }

  public void sendEmailRemindingToConfirmAccount(String to, String locale) throws MessagingException {
    sendMail(to,
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_REMOVED_REMINDER_SUBJECT),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_REMOVED_REMINDER_MESSAGE)
    );
  }

  public void sendMail(String to, String subject, String message) throws MessagingException {
    Session session = getSession();

    MimeMessage mail = new MimeMessage(session);
    mail.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    mail.setSubject(subject);
    mail.setText(message);

    Transport.send(mail);
  }

  private Properties getMailProperties() {
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.auth", true);
    props.put("mail.smtp.port", "465");

    return props;
  }

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
