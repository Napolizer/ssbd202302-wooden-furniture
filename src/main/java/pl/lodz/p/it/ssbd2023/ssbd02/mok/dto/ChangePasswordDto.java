package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Password;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {
  @Password
  @ToString.Exclude
  private String currentPassword;
  @Password
  @ToString.Exclude
  private String password;
}
