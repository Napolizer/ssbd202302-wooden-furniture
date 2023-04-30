package pl.lodz.p.it.ssbd2023.ssbd02.exceptions;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.*;
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

}
