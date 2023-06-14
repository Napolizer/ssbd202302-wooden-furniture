package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import jakarta.mail.MessagingException;

@Local
public interface MailServiceOperations {
  void sendEmailAboutOrderStateChange(String to, String locale, String orderProducts, String orderOldState,
                                      String orderNewState);
  void sendEmail(String to, String subject, String message) throws MessagingException;
}
