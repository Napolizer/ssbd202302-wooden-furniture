package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

public class LoginAlreadyExistsException extends Exception {
    public LoginAlreadyExistsException() {
        super("Account with given login already exists");
    }
}
