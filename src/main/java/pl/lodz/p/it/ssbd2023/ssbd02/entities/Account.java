package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Data
@AllArgsConstructor
@SuperBuilder
@Entity
public class Account extends AbstractEntity {
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

    @Column(nullable = false)
    private String locale;

    @Column(name = "failed_login_counter")
    private Integer failedLoginCounter;

    @Column(name = "blockade_end")
    private LocalDateTime blockadeEnd;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "account_state", nullable = false)
    private AccountState accountState;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "access_level_account",
            joinColumns = @JoinColumn(name = "access_level_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    @Builder.Default
    private List<AccessLevel> accessLevels = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
}
