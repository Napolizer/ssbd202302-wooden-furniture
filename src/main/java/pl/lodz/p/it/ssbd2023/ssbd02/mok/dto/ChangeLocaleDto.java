package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeLocaleDto {
  @NotNull
  private String locale;
}
