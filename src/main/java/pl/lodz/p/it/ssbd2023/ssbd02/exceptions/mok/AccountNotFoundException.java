package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException() {
        super("Account not found");
    }
}
