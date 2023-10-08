package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

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
@Entity(name = "employee")
@Table(name = "employee")
public class Employee extends AccessLevel {

  @Override
  public String getRoleName() {
    return EMPLOYEE;
  }
}
