package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class GoogleAccountInfoDto {
  private String firstName;
  private String lastName;
  private String login;
  private String email;
  private String locale;
  private String idToken;

  @Override
  public String toString() {
    return "GoogleAccountInfoDto{"
            + ", email='" + email + '\''
            + ", locale='" + locale + '\''
            + '}';
  }
}
