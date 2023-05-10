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
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.InvalidLinkException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.LoginAlreadyExistsException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.MoreThanOneAccessLevelAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.OldPasswordGivenException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.UnknownErrorException;
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
}
