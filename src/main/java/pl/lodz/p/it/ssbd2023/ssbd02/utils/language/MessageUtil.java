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
    public static final String EMAIL_EMAIL_CHANGE_SUBJECT = "mok.email.change.subject";
    public static final String ACCOUNT_ACCESS_LEVEL_ALREADY_ASSIGNED =
        "exception.mok.account.access.level.already.assigned";
    public static final String ACCOUNT_ACCESS_LEVEL_NOT_ASSIGNED =
        "exception.mok.account.access.level.not.assigned";
    public static final String ADDRESS_ALREADY_ASSIGNED =
            "exception.mok.account.address.already.assigned";
    public static final String ACCOUNT_NOT_FOUND = "exception.mok.account.not.found";
    public static final String ACCOUNT_NOT_ACTIVE = "exception.mok.account.not.active";
    public static final String ACCOUNT_EMAIL_ALREADY_EXISTS =
        "exception.mok.account.email.already.exists";
    public static final String ACCOUNT_CHANGE_STATE = "exception.mok.account.change.state";
    public static final String ACCOUNT_ACCESS_LEVEL = "exception.mok.account.access.level";
    public static final String ACCOUNT_LOGIN_ALREADY_EXISTS =
        "exception.mok.account.login.already.exists";
    public static final String ACCOUNT_ARCHIVE = "exception.mok.account.archive";
    public static final String ACCOUNT_BLOCKED = "exception.mok.account.blocked";
    public static final String ACCOUNT_INACTIVE = "exception.mok.account.inactive";
    public static final String ACCOUNT_CREDENTIALS = "exception.mok.account.credentials";
    public static final String ACCOUNT_OLD_PASSWORD = "exception.mok.account.old.password";
    public static final String ERROR_UNKNOWN_EXCEPTION = "exception.unknown.error";
    public static final String ERROR_OPTIMISTIC_LOCK = "exception.optimistic.lock";
    public static final String ERROR_GENERAL_PERSISTENCE = "exception.general.persistence";
    public static final String ERROR_ACCESS_DENIED = "exception.access.denied";
  }
}
