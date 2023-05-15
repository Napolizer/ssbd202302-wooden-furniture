package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity(name = "administrator")
public class Administrator extends AccessLevel {

  @Override
  public String getRoleName() {
    return ADMINISTRATOR;
  }
}
