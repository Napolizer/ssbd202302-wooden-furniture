package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@AllArgsConstructor
@Builder
public class Account {
    @Column(unique = true, updatable = false, nullable = false)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    @Column(name = "last_failed_login")
    private LocalDateTime lastFailedLogin;
    @Column(name = "last_login_ip_address")
    private String lastLoginIpAddress;
    private String locale;
    @Column(name = "failed_login_counter")
    private Integer failedLoginCounter;
    @Column(name = "blockade_end")
    private LocalDateTime blockadeEnd;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "account_state")
    private AccountState accountState;
}
