package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security;

import jakarta.security.enterprise.AuthenticationException;

public class AccountArchiveException extends AuthenticationException {
    public AccountArchiveException() {
        super("Account is archived");
    }
}
