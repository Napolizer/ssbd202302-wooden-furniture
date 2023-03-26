package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity(name = "access_level")
public class AccessLevel extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    private AccessLevelName level;

    private Boolean active;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;


}