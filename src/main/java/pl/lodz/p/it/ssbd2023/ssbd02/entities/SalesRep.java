package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity(name = "sales_rep")
@Table(name = "sales_rep")
public class SalesRep extends AccessLevel {

  @Override
  public String getRoleName() {
    return SALES_REP;
  }
}
