package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity(name = "access_level")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AccessLevel extends AbstractEntity {
    @ManyToMany
    @JoinTable(
            name = "access_level_account",
            joinColumns = @JoinColumn(name = "access_level_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> accounts = new ArrayList<>();

    public abstract String getGroupName();
}
