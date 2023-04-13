package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

public class AccessLevelAlreadyAssignedException extends Exception {
    public AccessLevelAlreadyAssignedException() {
        super("Given access Level already assigned");
    }
}
