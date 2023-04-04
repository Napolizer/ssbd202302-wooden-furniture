package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity(name = "access_level")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AccessLevel extends AbstractEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;
}
