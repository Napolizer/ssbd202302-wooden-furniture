package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity(name = "client")
public class Client extends AccessLevel {
    @OneToOne
    @JoinColumn(name = "company_id", nullable = true, unique = true)
    private Company company;

    @Override
    public String getGroupName() {
        return "CLIENTS";
    }
}
