package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException() {
        super("Account with given email already exists");
    }
}
