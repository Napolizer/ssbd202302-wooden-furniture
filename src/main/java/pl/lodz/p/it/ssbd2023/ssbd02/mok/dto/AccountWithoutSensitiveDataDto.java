package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;

@Getter
@Setter
public class AccountWithoutSensitiveDataDto {
  @NotNull
  private Long id;
  @NotNull
  private String login;
  @NotNull
  private String email;
  @NotNull
  private Boolean archive;
  private LocalDateTime lastLogin;
  private LocalDateTime lastFailedLogin;
  private String lastLoginIpAddress;
  @NotNull
  private String locale;
  private Integer failedLoginCounter;
  private LocalDateTime blockadeEnd;
  private AccountState accountState;
  private List<String> groups;

  public AccountWithoutSensitiveDataDto(Account account) {
    this.id = account.getId();
    this.login = account.getLogin();
    this.email = account.getEmail();
    this.archive = account.getArchive();
    this.lastLogin = account.getLastLogin();
    this.lastFailedLogin = account.getLastFailedLogin();
    this.lastLoginIpAddress = account.getLastLoginIpAddress();
    this.locale = account.getLocale();
    this.failedLoginCounter = account.getFailedLoginCounter();
    this.blockadeEnd = account.getBlockadeEnd();
    this.accountState = account.getAccountState();
    this.groups = account.getAccessLevels()
        .stream()
        .map(AccessLevel::getGroupName)
        .toList();
  }
}
