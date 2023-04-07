package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security;

import jakarta.security.enterprise.AuthenticationException;

public class AccountBlockedException extends AuthenticationException {
    public AccountBlockedException() {
        super("Account is blocked");
    }
}
