package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true, exclude = "account")
@NoArgsConstructor
@SuperBuilder
@Entity(name = "access_level")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype", columnDefinition = "varchar(31) NOT NULL")
@Table(name = "access_level", indexes = @Index(name = "access_level_account_id", columnList = "account_id"))
public abstract class AccessLevel extends AbstractEntity {
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "account_id", updatable = false, nullable = false)
  private Account account;

  public abstract String getGroupName();
}
