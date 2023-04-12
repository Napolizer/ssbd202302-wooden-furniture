package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

public class InvalidAccessLevelException extends Exception {
    public InvalidAccessLevelException() {
        super("Given access level is invalid");
    }
}
