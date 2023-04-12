package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

import java.util.Properties;

@Stateless
public class MailService {

    public void sendMailWithInfoAboutBlockingAccount(String to, String locale) throws MessagingException {
        sendMail(to,
                MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_BLOCKED_SUBJECT),
                MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_ACCOUNT_BLOCKED_MESSAGE)
        );
    }

    public void sendMailWithEmailChangeConfirmLink(String to, String locale, Long accountId) throws MessagingException {
        sendMail(to,
                MessageUtil.getMessage(locale, MessageUtil.MessageKey.EMAIL_EMAIL_CHANGE_SUBJECT),
                "http://localhost:8080/api/v1/accout/email/submit/" + accountId
                //link do podmiany na strone jak bedzie
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
        return Session.getDefaultInstance(getMailProperties(), new Authenticator() {
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
