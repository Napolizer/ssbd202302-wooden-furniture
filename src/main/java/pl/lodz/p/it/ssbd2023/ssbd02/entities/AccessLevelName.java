package pl.lodz.p.it.ssbd2023.ssbd02.entities;

public enum AccessLevelName {
    ADMIN("Administrator"),
    EMPLOYEE("Employee"),
    CLIENT("Client"),
    SALES_REP("Sales rep");

    public final String label;
    AccessLevelName(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
