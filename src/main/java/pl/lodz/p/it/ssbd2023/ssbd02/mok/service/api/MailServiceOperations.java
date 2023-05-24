package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api;

import jakarta.ejb.Local;
import jakarta.mail.MessagingException;

@Local
public interface MailServiceOperations {

  void sendMailWithInfoAboutBlockingAccount(String to, String locale)
          throws MessagingException;

  void sendMailWithInfoAboutActivatingAccount(String to, String locale)
          throws MessagingException;


  void sendMailWithInfoAboutConfirmingAccount(String to, String locale)
          throws MessagingException;

  void sendMailWithAccountConfirmationLink(String to, String locale, String token, String login)
          throws MessagingException;

  void sendResetPasswordMail(String to, String locale, String resetPasswordToken) throws MessagingException;

  void sendMailWithEmailChangeConfirmLink(String to, String locale, String token)
          throws MessagingException;

  void sendEmailAboutAddingAccessLevel(String to, String locale, String groupName) throws MessagingException;

  void sendMailWithPasswordChangeLink(String to, String locale, String token)
          throws MessagingException;

  void sendEmailAboutRemovingAccessLevel(String to, String locale, String groupName) throws MessagingException;

  void sendEmailAboutChangingAccessLevel(String to, String locale, String oldGroup, String newGroup)
          throws MessagingException;

  void sendEmailAboutRemovingNotVerifiedAccount(String to, String locale) throws MessagingException;

  void sendEmailAboutAdminSession(String to, String locale, String ip) throws MessagingException;

  void sendEmailRemindingToConfirmAccount(String to, String locale) throws MessagingException;

  void sendMail(String to, String subject, String message) throws MessagingException;
}
