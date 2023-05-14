package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Capitalized;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Login;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;

@Data
@Builder
public class AccountWithoutSensitiveDataDto {
  @NotNull
  private Long id;
  @Login
  private String login;
  @Email
  private String email;
  @Capitalized
  @ToString.Exclude
  private String firstName;
  @Capitalized
  @ToString.Exclude
  private String lastName;
  @NotNull
  private Boolean archive;
  private LocalDateTime lastLogin;
  private LocalDateTime lastFailedLogin;
  private String lastLoginIpAddress;
  private String lastFailedLoginIpAddress;
  @NotNull
  private String locale;
  @NotNull
  private Integer failedLoginCounter;
  @NotNull
  private LocalDateTime blockadeEnd;
  @NotNull
  private AccountState accountState;
  @NotNull
  private List<String> groups;
  @NotNull
  @Valid
  @ToString.Exclude
  private AddressDto address;
  @NotNull
  private String hash;
}
