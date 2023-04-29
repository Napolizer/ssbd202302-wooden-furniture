package pl.lodz.p.it.ssbd2023.ssbd02.exceptions;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessDeniedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelAlreadyAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelNotAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotActiveException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.ApplicationOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.EmailAlreadyExistsException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.IllegalAccountStateChangeException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.InvalidAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.LoginAlreadyExistsException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.OldPasswordGivenException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.UnknownErrorException;
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


}