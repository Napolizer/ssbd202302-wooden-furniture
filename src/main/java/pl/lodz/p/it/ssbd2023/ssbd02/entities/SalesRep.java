package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity(name = "sales_rep")
public class SalesRep extends AccessLevel {

  @Override
  public String getGroupName() {
    return "SALES_REPS";
  }
}
