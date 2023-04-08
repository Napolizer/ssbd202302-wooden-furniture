package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

public class IllegalAccountStateChangeException extends Exception {
    public IllegalAccountStateChangeException() {
        super("The requested account state change cannot be performed due to restrictions or the account is already " +
                "in the desired state.");
    }
}
