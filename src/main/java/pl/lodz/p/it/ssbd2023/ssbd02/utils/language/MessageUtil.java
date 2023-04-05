package pl.lodz.p.it.ssbd2023.ssbd02.utils.language;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUtil {
    public static String getMessage(String locale, String messageKey) {
        ResourceBundle bundle = ResourceBundle
                .getBundle("messages", Locale.forLanguageTag(locale));
        return bundle.getString(messageKey);
    }

    public static class MessageKey {
        public static final String EMAIL_ACCOUNT_BLOCKED_SUBJECT = "mok.email.account.blocked.subject";
        public static final String EMAIL_ACCOUNT_BLOCKED_MESSAGE = "mok.email.account.blocked.message";
    }
}
