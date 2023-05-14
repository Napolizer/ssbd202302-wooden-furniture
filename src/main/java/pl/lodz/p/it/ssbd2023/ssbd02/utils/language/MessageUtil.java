package pl.lodz.p.it.ssbd2023.ssbd02.utils.language;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUtil {

  static {
    Locale.setDefault(new Locale("default"));
  }

  public static String getMessage(String locale, String messageKey) {
    ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale));
    return bundle.getString(messageKey);
  }

  public static class MessageKey {
    public static final String EMAIL_ACCOUNT_BLOCKED_SUBJECT = "mok.email.account.blocked.subject";
    public static final String EMAIL_ACCOUNT_BLOCKED_MESSAGE = "mok.email.account.blocked.message";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_ADDED_SUBJECT =
            "mok.email.account.access.level.added.subject";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_ADDED_MESSAGE =
            "mok.email.account.access.level.added.message";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_SUBJECT =
            "mok.email.account.access.level.removed.subject";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_MESSAGE =
            "mok.email.account.access.level.removed.message";
    public static final String EMAIL_EMAIL_CHANGE_SUBJECT = "mok.email.change.subject";
    public static final String EMAIL_EMAIL_CHANGE_TOPIC = "mok.email.change.topic";
    public static final String EMAIL_ACCOUNT_CONFIRMATION_SUBJECT = "mok.email.account.confirmation.subject";
    public static final String EMAIL_ACCOUNT_CONFIRMATION_TOPIC1 = "mok.email.account.confirmation.topic1";
    public static final String EMAIL_ACCOUNT_CONFIRMATION_TOPIC2 = "mok.email.account.confirmation.topic2";
    public static final String EMAIL_ACCOUNT_CONFIRMATION_TOPIC3 = "mok.email.account.confirmation.topic3";
    public static final String EMAIL_ACCOUNT_REMOVED_SUBJECT = "mok.email.account.removed.subject";
    public static final String EMAIL_ACCOUNT_REMOVED_MESSAGE = "mok.email.account.removed.message";
    public static final String EMAIL_ACCOUNT_REMOVED_REMINDER_SUBJECT = "mok.email.account.removed.reminder.subject";
    public static final String EMAIL_ACCOUNT_REMOVED_REMINDER_MESSAGE = "mok.email.account.removed.reminder.message";
    public static final String ACCOUNT_ACCESS_LEVEL_ALREADY_ASSIGNED =
        "exception.mok.account.access.level.already.assigned";
    public static final String ACCOUNT_ACCESS_LEVEL_NOT_ASSIGNED =
        "exception.mok.account.access.level.not.assigned";
    public static final String ACCOUNT_ADMINISTRATOR_ACCESS_LEVEL_ALREADY_ASSIGNED =
            "exception.mok.account.administrator.access.level.already.assigned";
    public static final String ACCOUNT_CLIENT_AND_SALES_REP_ACCESS_LEVEL_CONFLICT =
            "exception.mok.account.client.and.sales.rep.access.levels.conflict";
    public static final String ACCOUNT_REMOVE_ACCESS_LEVEL = "exception.mok.account.remove.access.level";
    public static final String ADDRESS_ALREADY_ASSIGNED =
            "exception.mok.account.address.already.assigned";
    public static final String ACCOUNT_NOT_FOUND = "exception.mok.account.not.found";
    public static final String ACCOUNT_NOT_ACTIVE = "exception.mok.account.not.active";
    public static final String ACCOUNT_NOT_VERIFIED = "exception.mok.account.not.verified";
    public static final String ACCOUNT_EMAIL_ALREADY_EXISTS =
        "exception.mok.account.email.already.exists";
    public static final String COMPANY_NIP_ALREADY_EXISTS =
            "exception.mok.company.nip.already.exists";
    public static final String ACCOUNT_CHANGE_STATE = "exception.mok.account.change.state";
    public static final String ACCOUNT_ACCESS_LEVEL = "exception.mok.account.access.level";
    public static final String ACCOUNT_LOGIN_ALREADY_EXISTS =
        "exception.mok.account.login.already.exists";
    public static final String ACCOUNT_MORE_THAN_ONE_ACCESS_LEVEL =
            "exception.mok.account.access.level.many.assigned";
    public static final String ACCOUNT_ARCHIVE = "exception.mok.account.archive";
    public static final String ACCOUNT_BLOCKED = "exception.mok.account.blocked";
    public static final String ACCOUNT_INACTIVE = "exception.mok.account.inactive";
    public static final String ACCOUNT_ALREADY_VERIFIED = "exception.mok.account.already.verified";
    public static final String ACCOUNT_CREDENTIALS = "exception.mok.account.credentials";
    public static final String ACCOUNT_OLD_PASSWORD = "exception.mok.account.old.password";
    public static final String ERROR_UNKNOWN_EXCEPTION = "exception.unknown.error";
    public static final String ERROR_OPTIMISTIC_LOCK = "exception.optimistic.lock";
    public static final String ERROR_GENERAL_PERSISTENCE = "exception.general.persistence";
    public static final String ERROR_ACCESS_DENIED = "exception.access.denied";
    public static final String ERROR_MAIL_SERVICE = "exception.mail.service";
    public static final String ERROR_EXPIRED_ACCOUNT_CONFIRMATION_LINK = "exception.expired.account.confirmation.link";
    public static final String ERROR_EXPIRED_PASSWORD_RESET_LINK = "exception.expired.password.reset.link";
    public static final String ERROR_EXPIRED_CHANGE_EMAIL_LINK = "exception.expired.change.email.link";
    public static final String ERROR_INVALID_LINK = "exception.invalid.link";
    public static final String ERROR_FORBIDDEN = "exception.forbidden";
    public static final String ACCOUNT_EMAIL_DOES_NOT_EXIST = "exception.mok.account.email.does.not.exist";
    public static final String EMAIL_RESET_PASSWORD_SUBJECT = "mok.email.account.reset.password.subject";
    public static final String EMAIL_RESET_PASSWORD_MESSAGE1 = "mok.email.account.reset.password.message1";
    public static final String EMAIL_RESET_PASSWORD_MESSAGE2 = "mok.email.account.reset.password.message2";
  }
}
