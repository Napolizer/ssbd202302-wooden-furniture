package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api;

import jakarta.ejb.Local;
import jakarta.mail.MessagingException;

@Local
public interface MailServiceOperations {

  void sendEmailWithInfoAboutBlockingAccount(String to, String locale);

  void sendEmailWithInfoAboutActivatingAccount(String to, String locale);

  void sendEmailWithInfoAboutConfirmingAccount(String to, String locale);

  void sendEmailWithAccountConfirmationLink(String to, String locale, String token, String login);

  void sendResetPasswordEmail(String to, String locale, String resetPasswordToken);

  void sendEmailWithEmailChangeConfirmLink(String to, String locale, String token);

  void sendEmailAboutAddingAccessLevel(String to, String locale, String groupName);

  void sendEmailWithPasswordChangeLink(String to, String locale, String token);

  void sendEmailAboutRemovingAccessLevel(String to, String locale, String groupName);

  void sendEmailAboutChangingAccessLevel(String to, String locale, String oldGroup, String newGroup);

  void sendEmailAboutRemovingNotVerifiedAccount(String to, String locale);

  void sendEmailAboutAdminSession(String to, String locale, String ip);

  void sendEmailAboutChangingOrderState(String to, String locale);

  void sendEmail(String to, String subject, String message) throws MessagingException;
}
