package pl.lodz.p.it.ssbd2023.ssbd02.utils.language;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUtil {

  public static String LOCALE_PL = "pl";
  public static String LOCALE_EN = "en";

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
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_ADDED_MESSAGE1 =
            "mok.email.account.access.level.added.message1";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_ADDED_MESSAGE2 =
            "mok.email.account.access.level.added.message2";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_SUBJECT =
            "mok.email.account.access.level.removed.subject";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_MESSAGE1 =
            "mok.email.account.access.level.removed.message1";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_REMOVED_MESSAGE2 =
            "mok.email.account.access.level.removed.message2";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_CHANGED_SUBJECT =
            "mok.email.account.access.level.changed.subject";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_CHANGED_MESSAGE1 =
            "mok.email.account.access.level.changed.message1";
    public static final String EMAIL_ACCOUNT_ACCESS_LEVEL_CHANGED_MESSAGE2 =
            "mok.email.account.access.level.changed.message2";
    public static final String EMAIL_EMAIL_CHANGE_SUBJECT = "mok.email.change.subject";
    public static final String EMAIL_EMAIL_CHANGE_TOPIC = "mok.email.change.topic";
    public static final String EMAIL_ACCOUNT_CONFIRMATION_SUBJECT = "mok.email.account.confirmation.subject";
    public static final String EMAIL_ACCOUNT_CONFIRMATION_TOPIC1 = "mok.email.account.confirmation.topic1";
    public static final String EMAIL_ACCOUNT_CONFIRMATION_TOPIC2 = "mok.email.account.confirmation.topic2";
    public static final String EMAIL_ACCOUNT_CONFIRMATION_TOPIC3 = "mok.email.account.confirmation.topic3";
    public static final String EMAIL_ACCOUNT_REMOVED_SUBJECT = "mok.email.account.removed.subject";
    public static final String EMAIL_ACCOUNT_REMOVED_MESSAGE = "mok.email.account.removed.message";
    public static final String EMAIL_ACCOUNT_ACTIVATED_SUBJECT = "mok.email.account.activated.subject";
    public static final String EMAIL_ACCOUNT_ACTIVATED_MESSAGE = "mok.email.account.activated.message";
    public static final String EMAIL_ACCOUNT_VERIFIED_SUBJECT = "mok.email.account.verified.subject";
    public static final String EMAIL_ACCOUNT_VERIFIED_MESSAGE = "mok.email.account.verified.message";
    public static final String EMAIL_ACCOUNT_CHANGE_PASSWORD_SUBJECT = "mok.email.account.change.password.subject";
    public static final String EMAIL_ACCOUNT_CHANGE_PASSWORD_TOPIC = "mok.email.account.change.password.topic";
    public static final String ACCOUNT_ACCESS_LEVEL_ALREADY_ASSIGNED =
        "exception.mok.account.access.level.already.assigned";
    public static final String ACCOUNT_ACCESS_LEVEL_NOT_ASSIGNED =
        "exception.mok.account.access.level.not.assigned";
    public static final String ACCOUNT_ADMINISTRATOR_ACCESS_LEVEL_ALREADY_ASSIGNED =
            "exception.mok.account.administrator.access.level.already.assigned";
    public static final String ACCOUNT_CLIENT_AND_SALES_REP_ACCESS_LEVEL_CONFLICT =
            "exception.mok.account.client.and.sales.rep.access.levels.conflict";
    public static final String ACCOUNT_REMOVE_ACCESS_LEVEL = "exception.mok.account.remove.access.level";
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
    public static final String ERROR_GOOGLE_CONFLICT = "exception.google.conflict";
    public static final String ERROR_INVALID_ACCOUNT_TYPE = "exception.invalid.account.type";
    public static final String ERROR_INVALID_TIME_ZONE = "exception.invalid.time.zone";
    public static final String ERROR_GITHUB_CONFLICT = "exception.github.conflict";
    public static final String ACCOUNT_EXTERNAL_INVALID_STATE = "exception.mok.account.external.invalid.state";
    public static final String ACCOUNT_EMAIL_DOES_NOT_EXIST = "exception.mok.account.email.does.not.exist";
    public static final String EMAIL_RESET_PASSWORD_SUBJECT = "mok.email.account.reset.password.subject";
    public static final String EMAIL_RESET_PASSWORD_MESSAGE1 = "mok.email.account.reset.password.message1";
    public static final String EMAIL_RESET_PASSWORD_MESSAGE2 = "mok.email.account.reset.password.message2";
    public static final String EMAIL_ADMIN_LOGIN_SESSION_SUBJECT = "mok.email.admin.login.subject";
    public static final String EMAIL_ADMIN_LOGIN_SESSION_MESSAGE = "mok.email.admin.login.message";
    public static final String EMAIL_ORDER_STATE_CHANGE_SUBJECT = "moz.email.order.state.change.subject";
    public static final String EMAIL_ORDER_STATE_CHANGE_MESSAGE1 = "moz.email.order.state.change.message1";
    public static final String EMAIL_ORDER_STATE_CHANGE_MESSAGE2 = "moz.email.order.state.change.message2";
    public static final String EMAIL_ORDER_STATE_CHANGE_MESSAGE3 = "moz.email.order.state.change.message3";
    public static final String ERROR_EXPIRED_REFRESH_TOKEN = "exception.expired.refresh.token";
    public static final String ERROR_INVALID_REFRESH_TOKEN = "exception.invalid.refresh.token";
    public static final String ERROR_PASSWORD_ALREADY_USED = "exception.password.already.used";
    public static final String ERROR_INVALID_CURRENT_PASSWORD = "exception.current.password.invalid";
    public static final String ERROR_INVALID_MODE = "exception.mok.invalid.mode";
    public static final String TRANSACTION_ROLLBACK = "exception.transaction.rollback";

    public static final String CATEGORY_NOT_FOUND = "exception.moz.category.not.found";
    public static final String PARENT_CATEGORY_NOT_ALLOWED = "exception.moz.parent.category.not.allowed";
    public static final String PRODUCT_GROUP_ALREADY_EXITS = "exception.moz.product.group.already.exists";
    public static final String PRODUCT_GROUP_ALREADY_ARCHIVED = "exception.moz.product.group.already.archived";
    public static final String PRODUCT_GROUP_ALREADY_ACTIVATED = "exception.moz.product.group.already.activated";
    public static final String PRODUCT_ALREADY_EXITS = "exception.moz.product.dimensions.already.exists";
    public static final String PRODUCT_NOT_FOUND = "exception.moz.product.not.found";
    public static final String PRODUCT_IS_ARCHIVE = "exception.moz.product.is.archive";
    public static final String PRODUCT_GROUP_IS_ARCHIVE = "exception.moz.product.group.is.archive";
    public static final String PRODUCT_GROUP_NOT_FOUND = "exception.moz.product.group.not.found";
    public static final String ORDER_ALREADY_EXITS = "exception.moz.order.already.exists";
    public static final String PRODUCT_CREATE_DTO_VALIDATION = "exception.moz.product.create.dto.validation";
    public static final String INVALID_IMAGE_FILE_FORMAT = "exception.invalid.image.file.format";
    public static final String INVALID_COLOR = "exception.moz.invalid.color";
    public static final String INVALID_WOOD_TYPE = "exception.moz.invalid.wood.type";
    public static final String INVALID_DATE = "exception.moz.invalid.date";
    public static final String INVALID_LOCALE = "exception.moz.invalid.locale";
    public static final String INCOMPATIBLE_PRODUCT_IMAGE = "exception.moz.incompatible.product.image";
    public static final String ORDER_ALREADY_IN_DELIVERY = "exception.moz.order.already.in.delivery";
    public static final String ORDER_ALREADY_DELIVERED = "exception.moz.order.already.delivered";
    public static final String ORDER_ALREADY_CANCELLED = "exception.moz.order.already.cancelled";
    public static final String ORDER_ALREADY_OBSERVED = "exception.moz.order.already.observed";
    public static final String ORDER_CANCELLED = "exception.moz.order.cancelled";
    public static final String ORDER_DELIVERED = "exception.moz.order.delivered";

    public static final String ORDER_NOT_FOUND = "exception.moz.order.not.found";
    public static final String PRODUCT_ALREADY_RATED = "exception.moz.product.rate.exists";
    public static final String RATE_NOT_FOUND = "exception.moz.rate.notfound";
    public static final String PRODUCT_CHANGE_STATE_DEARCHIVE = "exception.moz.product.change.state.dearchive";
    public static final String PRODUCT_CHANGE_STATE_ARCHIVE = "exception.moz.product.change.state.archive";
    public static final String INVALID_ORDER_STATE_TRANSITION = "exception.moz.invalid.order.state.transition";
    public static final String INVALID_PRODUCT_AMOUNT = "exception.moz.invalid.product.amount";
    public static final String PRODUCT_UPDATED_BY = "exception.moz.product.updated.by";
    public static final String PRODUCT_CREATED_BY = "exception.moz.product.created.by";

    public static final String REPORT_TITLE1 = "moz.report.title1";
    public static final String REPORT_TITLE2 = "moz.report.title2";
    public static final String REPORT_HEADER1 = "moz.report.header1";
    public static final String REPORT_HEADER2 = "moz.report.header2";
    public static final String REPORT_HEADER3 = "moz.report.header3";
    public static final String REPORT_HEADER4 = "moz.report.header4";
    public static final String REPORT_HEADER5 = "moz.report.header5";
  }
}
