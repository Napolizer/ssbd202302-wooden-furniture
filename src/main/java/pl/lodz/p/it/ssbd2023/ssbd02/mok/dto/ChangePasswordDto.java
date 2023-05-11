package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import lombok.Data;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Password;

@Data
public class ChangePasswordDto {
  @Password
  @ToString.Exclude
  private String password;
}
