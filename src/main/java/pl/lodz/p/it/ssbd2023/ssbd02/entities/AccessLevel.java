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
@DiscriminatorColumn(name = "dtype", columnDefinition = "varchar(31) NOT NULL")
@Table(name = "access_level", indexes = @Index(name = "access_level_account_id", columnList = "account_id", unique = true))
public abstract class AccessLevel extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "account_id", updatable = false, nullable = false)
    private Account account;

    public abstract String getGroupName();
}
