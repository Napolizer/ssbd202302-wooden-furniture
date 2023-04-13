package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

public class AccessLevelNotAssignedException extends Exception {
    public AccessLevelNotAssignedException() {
        super("Given access level is not assigned");
    }
}
