package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Capitalized;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Locale;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Login;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Nip;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Password;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.PostalCode;

@Data
@SuperBuilder
@NoArgsConstructor
public class AccountRegisterDto {
  @Capitalized
  private String firstName;

  @Capitalized
  private String lastName;

  @Capitalized
  private String country;

  @Capitalized
  private String city;

  @Capitalized
  private String street;

  @NotNull
  @Positive(message = "Street number must be a positive integer")
  private Integer streetNumber;

  @PostalCode
  private String postalCode;

  @Login
  private String login;

  @Password
  private String password;

  @NotNull
  @Email(message = "Invalid email address format")
  private String email;

  @Locale
  private String locale;

  @Nip
  private String nip;

  @Company
  private String companyName;

  @Override
  public String toString() {
    return "AccountRegisterDto{"
            + "login='" + login + '\''
            + ", email='" + email + '\''
            + ", locale='" + locale + '\''
            + '}';
  }
}
