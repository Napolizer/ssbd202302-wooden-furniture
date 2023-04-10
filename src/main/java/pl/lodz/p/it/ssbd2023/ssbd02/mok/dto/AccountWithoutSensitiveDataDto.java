package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;

import java.time.LocalDateTime;
import java.util.List;

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
    @NotNull
    private AccountState accountState;
    @NotNull
    private List<AccessLevel> accessLevels;

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
        this.accessLevels = account.getAccessLevels();
    }
}
