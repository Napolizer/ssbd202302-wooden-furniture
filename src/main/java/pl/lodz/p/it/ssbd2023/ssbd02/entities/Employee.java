package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity(name = "employee")
public class Employee extends AccessLevel {

  @Override
  public String getGroupName() {
    return "EMPLOYEES";
  }
}
