package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security;

import jakarta.security.enterprise.AuthenticationException;

public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
