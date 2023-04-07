package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security;

import jakarta.security.enterprise.AuthenticationException;

public class AccountIsInactiveException extends AuthenticationException {
    public AccountIsInactiveException() {
        super("Account is inactive");
    }
}
