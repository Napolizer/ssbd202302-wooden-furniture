package pl.lodz.p.it.ssbd2023.ssbd02.exceptions;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessDeniedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelAlreadyAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelNotAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountAlreadyVerifiedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotActiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotVerifiedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AdministratorAccessLevelAlreadyAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.ApplicationOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.ClientAndSalesRepAccessLevelsConflictException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.EmailAlreadyExistsException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.EmailNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.ExpiredLinkException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.IllegalAccountStateChangeException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.InvalidAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.InvalidCurrentPasswordException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.InvalidLinkException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.LoginAlreadyExistsException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.MoreThanOneAccessLevelAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.NipAlreadyExistsException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.OldPasswordGivenException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.RemoveAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.UnknownErrorException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.CategoryNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.OrderAlreadyCancelledException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.OrderAlreadyDeliveredException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.OrderAlreadyExistsException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.OrderAlreadyInDeliveryException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.OrderAlreadyObservedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.OrderNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.ParentCategoryNotAllowedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.ProductGroupAlreadyExistsException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.ProductNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountArchiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountBlockedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.AccountIsInactiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.InvalidCredentialsException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public final class ApplicationExceptionFactory {
  public static AccessLevelAlreadyAssignedException createAccessLevelAlreadyAssignedException() {
    return new AccessLevelAlreadyAssignedException();
  }

  public static AccessLevelNotAssignedException createAccessLevelNotAssignedException() {
    return new AccessLevelNotAssignedException();
  }

  public static AccountNotVerifiedException createAccountNotVerifiedException() {
    return new AccountNotVerifiedException();
  }

  public static AccountNotFoundException createAccountNotFoundException() {
    return new AccountNotFoundException();
  }

  public static EmailAlreadyExistsException createEmailAlreadyExistsException(Throwable cause) {
    return new EmailAlreadyExistsException(cause);
  }

  public static EmailAlreadyExistsException createEmailAlreadyExistsException() {
    return new EmailAlreadyExistsException();
  }

  public static NipAlreadyExistsException createNipAlreadyExistsException(Throwable cause) {
    return new NipAlreadyExistsException(cause);
  }

  public static IllegalAccountStateChangeException createIllegalAccountStateChangeException() {
    return new IllegalAccountStateChangeException();
  }

  public static InvalidAccessLevelException createInvalidAccessLevelException() {
    return new InvalidAccessLevelException();
  }

  public static MoreThanOneAccessLevelAssignedException createMoreThanOneAccessLevelAssignedException() {
    return new MoreThanOneAccessLevelAssignedException();
  }

  public static LoginAlreadyExistsException createLoginAlreadyExistsException(Throwable cause) {
    return new LoginAlreadyExistsException(cause);
  }

  public static AccountNotActiveException createAccountNotActiveException() {
    return new AccountNotActiveException();
  }

  public static OldPasswordGivenException createOldPasswordGivenException() {
    return new OldPasswordGivenException();
  }

  public static UnknownErrorException createUnknownErrorException(Throwable cause) {
    return new UnknownErrorException(cause);
  }

  public static ApplicationOptimisticLockException createApplicationOptimisticLockException() {
    return new ApplicationOptimisticLockException();
  }

  public static BaseWebApplicationException createGeneralPersistenceException(Throwable cause) {
    return new BaseWebApplicationException(MessageUtil.MessageKey.ERROR_GENERAL_PERSISTENCE,
        cause, Response.Status.INTERNAL_SERVER_ERROR);
  }

  public static BaseWebApplicationException createMailServiceException(Throwable cause) {
    return new BaseWebApplicationException(MessageUtil.MessageKey.ERROR_MAIL_SERVICE,
            cause, Response.Status.INTERNAL_SERVER_ERROR);
  }

  public static BaseWebApplicationException createInvalidExternalAccountStateException() {
    return new BaseWebApplicationException(MessageUtil.MessageKey.ACCOUNT_EXTERNAL_INVALID_STATE,
            Response.Status.CONFLICT);
  }

  public static BaseWebApplicationException createGoogleOauthConflictException() {
    return new BaseWebApplicationException(MessageUtil.MessageKey.ERROR_GOOGLE_CONFLICT, Response.Status.CONFLICT);
  }

  public static BaseWebApplicationException createInvalidAccountTypeException() {
    return new BaseWebApplicationException(MessageUtil.MessageKey.ERROR_INVALID_ACCOUNT_TYPE,
            Response.Status.FORBIDDEN);
  }

  public static BaseWebApplicationException createInvalidTimeZoneException() {
    return new BaseWebApplicationException(MessageUtil.MessageKey.ERROR_INVALID_TIME_ZONE,
            Response.Status.BAD_REQUEST);
  }

  public static AccessDeniedException createAccessDeniedException(Throwable cause) {
    return new AccessDeniedException(cause);
  }

  public static AccountArchiveException createAccountArchiveException()  {
    return new AccountArchiveException();
  }

  public static AccountBlockedException createAccountBlockedException() {
    return new AccountBlockedException();
  }

  public static AccountIsInactiveException createAccountIsInactiveException() {
    return new AccountIsInactiveException();
  }

  public static InvalidCredentialsException createInvalidCredentialsException() {
    return new InvalidCredentialsException();
  }

  public static ExpiredLinkException createAccountConfirmationExpiredLinkException() {
    return new ExpiredLinkException(MessageUtil.MessageKey.ERROR_EXPIRED_ACCOUNT_CONFIRMATION_LINK);
  }

  public static ExpiredLinkException createPasswordResetExpiredLinkException() {
    return new ExpiredLinkException(MessageUtil.MessageKey.ERROR_EXPIRED_PASSWORD_RESET_LINK);
  }

  public static ExpiredLinkException createChangeEmailExpiredLinkException() {
    return new ExpiredLinkException(MessageUtil.MessageKey.ERROR_EXPIRED_CHANGE_EMAIL_LINK);
  }

  public static BaseWebApplicationException createForbiddenException() {
    return new BaseWebApplicationException(MessageUtil.MessageKey.ERROR_FORBIDDEN,
            Response.Status.FORBIDDEN);
  }

  public static InvalidLinkException createInvalidLinkException() {
    return new InvalidLinkException();
  }

  public static AccountAlreadyVerifiedException createAccountAlreadyVerifiedException() {
    return new AccountAlreadyVerifiedException();
  }

  public static EmailNotFoundException createEmailNotFoundException() {
    return new EmailNotFoundException();
  }

  public static AdministratorAccessLevelAlreadyAssignedException
      createAdministratorAccessLevelAlreadyAssignedException() {
    return new AdministratorAccessLevelAlreadyAssignedException();
  }

  public static ClientAndSalesRepAccessLevelsConflictException
      createClientAndSalesRepAccessLevelsConflictException() {
    return new ClientAndSalesRepAccessLevelsConflictException();
  }

  public static RemoveAccessLevelException createRemoveAccessLevelException() {
    return new RemoveAccessLevelException();
  }

  public static BaseWebApplicationException createGithubOauthConflictException() {
    return new BaseWebApplicationException(MessageUtil.MessageKey.ERROR_GITHUB_CONFLICT, Response.Status.CONFLICT);
  }

  public static BaseWebApplicationException expiredRefreshTokenException() {
    return new BaseWebApplicationException(
        MessageUtil.MessageKey.ERROR_EXPIRED_REFRESH_TOKEN, Response.Status.UNAUTHORIZED);
  }

  public static BaseWebApplicationException invalidRefreshTokenException() {
    return new BaseWebApplicationException(
        MessageUtil.MessageKey.ERROR_INVALID_REFRESH_TOKEN, Response.Status.UNAUTHORIZED);
  }

  public static BaseWebApplicationException passwordAlreadyUsedException() {
    return new BaseWebApplicationException(
        MessageUtil.MessageKey.ERROR_PASSWORD_ALREADY_USED, Response.Status.CONFLICT);
  }

  public static InvalidCurrentPasswordException createInvalidCurrentPasswordException() {
    return new InvalidCurrentPasswordException(MessageUtil.MessageKey.ERROR_INVALID_CURRENT_PASSWORD,
            Response.Status.BAD_REQUEST);
  }

  public static BaseWebApplicationException createInvalidModeException() {
    return new BaseWebApplicationException(
        MessageUtil.MessageKey.ERROR_INVALID_MODE, Response.Status.BAD_REQUEST);
  }

  public static ApplicationTransactionRollbackException createApplicationTransactionRollbackException() {
    return new ApplicationTransactionRollbackException();
  }

  public static CategoryNotFoundException createCategoryNotFoundException() {
    return new CategoryNotFoundException();
  }

  public static ProductGroupAlreadyExistsException createProductGroupAlreadyExistsException() {
    return new ProductGroupAlreadyExistsException();
  }

  public static OrderAlreadyExistsException createOrderAlreadyExistsException() {
    return new OrderAlreadyExistsException();
  }

  public static OrderAlreadyCancelledException createOrderAlreadyCancelledException() {
    return new OrderAlreadyCancelledException();
  }

  public static OrderAlreadyInDeliveryException createOrderAlreadyInDeliveryException() {
    return new OrderAlreadyInDeliveryException();
  }

  public static OrderAlreadyDeliveredException createOrderAlreadyDeliveredException() {
    return new OrderAlreadyDeliveredException();
  }

  public static OrderNotFoundException createOrderNotFoundException() {
    return new OrderNotFoundException();
  }

  public static OrderAlreadyObservedException createOrderAlreadyObservedException() {
    return new OrderAlreadyObservedException();
  }

  public static ParentCategoryNotAllowedException createParentCategoryNotAllowedException() {
    return new ParentCategoryNotAllowedException();
  }

  public static ProductNotFoundException createProductNotFoundException() {
    return new ProductNotFoundException();
  }
}
