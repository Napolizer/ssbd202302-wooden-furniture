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
@NamedQueries({
        @NamedQuery(name = Account.FIND_BY_ACCOUNT_ID,
                query = "SELECT account FROM Account account WHERE account.id = :id"),
        @NamedQuery(name = Account.FIND_BY_LOGIN,
                query = "SELECT account FROM Account account WHERE account.login = :login"),
        @NamedQuery(name = Account.FIND_BY_EMAIL,
                query = "SELECT account FROM Account account WHERE account.email = :email"),
        @NamedQuery(name = Account.FIND_ALL_BY_FIRST_NAME,
                query = "SELECT account FROM Account account WHERE account.person.firstName = :firstName"),
        @NamedQuery(name = Account.FIND_ALL_BY_LAST_NAME,
                query = "SELECT account FROM Account account WHERE account.person.lastName = :lastName"),
        @NamedQuery(name = Account.FIND_ALL_BY_ADDRESS_ID,
                query = "SELECT account FROM Account account WHERE account.person.address.id = :addressId")
})
public class Account extends AbstractEntity {
    public static final String FIND_ALL_BY_FIRST_NAME = "Account.findAllByFirstName";
    public static final String FIND_ALL_BY_LAST_NAME = "Account.findAllByLastName";
    public static final String FIND_BY_LOGIN = "Account.findByLogin";
    public static final String FIND_BY_EMAIL = "Account.findByEmail";
    public static final String FIND_ALL_BY_ADDRESS_ID = "Account.findAllByAddressId";
    public static final String FIND_BY_ACCOUNT_ID = "Account.findByAccountId";
    @Column(unique = true, updatable = false, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "person_id", unique = true, nullable = false)
    private Person person;

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

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<AccessLevel> accessLevels = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

//    private String newEmail;
}
