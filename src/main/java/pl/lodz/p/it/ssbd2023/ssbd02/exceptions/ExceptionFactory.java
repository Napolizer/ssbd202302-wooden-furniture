package pl.lodz.p.it.ssbd2023.ssbd02.exceptions;

import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.*;

public final class ExceptionFactory {
    public static AccessLevelAlreadyAssignedException createAccessLevelAlreadyAssigned() {
        return new AccessLevelAlreadyAssignedException();
    }

    public static AccessLevelNotAssignedException createAccessLevelNotAssigned() {
        return new AccessLevelNotAssignedException();
    }

    public static AccountNotFoundException createAccountNotFound() {
        return new AccountNotFoundException();
    }

    public static EmailAlreadyExistsException createEmailAlreadyExists(Throwable cause) {
        return new EmailAlreadyExistsException(cause);
    }

    public static IllegalAccountStateChangeException createIllegalAccountStateChange() {
        return new IllegalAccountStateChangeException();
    }

    public static InvalidAccessLevelException createInvalidAccessLevel() {
        return new InvalidAccessLevelException();
    }

    public static LoginAlreadyExistsException createLoginAlreadyExists(Throwable cause) {
        return new LoginAlreadyExistsException(cause);
    }

    public static AccountNotActiveException createAccountNotActive() {
        return new AccountNotActiveException();
    }

    public static OldPasswordGivenException createOldPasswordGiven() {
        return new OldPasswordGivenException();
    }

    public static UnknownException createUnknown(Throwable cause) {
        return new UnknownException(cause);
    }
}
