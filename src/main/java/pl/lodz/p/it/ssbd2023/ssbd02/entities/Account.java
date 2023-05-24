package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Data
@ToString
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
        query = "SELECT account FROM Account account WHERE account.person.address.id = :addressId"),
    @NamedQuery(name = Account.FIND_ALL_BY_FULL_NAME_LIKE,
            query =   "SELECT account FROM Account account "
                    + "WHERE LOWER(CONCAT(account.person.firstName, ' ', account.person.lastName)) "
                    + "LIKE LOWER(CONCAT('%', :fullName, '%'))"),
})
public class Account extends AbstractEntity {
  public static final String FIND_ALL_BY_FIRST_NAME = "Account.findAllByFirstName";
  public static final String FIND_ALL_BY_LAST_NAME = "Account.findAllByLastName";
  public static final String FIND_BY_LOGIN = "Account.findByLogin";
  public static final String FIND_BY_EMAIL = "Account.findByEmail";
  public static final String FIND_ALL_BY_ADDRESS_ID = "Account.findAllByAddressId";
  public static final String FIND_BY_ACCOUNT_ID = "Account.findByAccountId";
  public static final String FIND_ALL_BY_FULL_NAME_LIKE = "Account.findAllByFullName";
  @Column(unique = true, updatable = false, nullable = false)
  private String login;

  @Column(nullable = false)
  @ToString.Exclude
  private String password;

  @Column(unique = true, nullable = false)
  @ToString.Exclude
  private String email;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
  @JoinColumn(name = "person_id", unique = true, nullable = false)
  @ToString.Exclude
  private Person person;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  @Column(name = "last_failed_login")
  private LocalDateTime lastFailedLogin;

  @Column(name = "last_login_ip_address")
  private String lastLoginIpAddress;

  @Column(name = "last_failed_login_ip_address")
  private String lastFailedLoginIpAddress;

  @Column(nullable = false)
  private String locale;

  @Column(name = "failed_login_counter")
  private Integer failedLoginCounter;

  @Column(name = "blockade_end")
  private LocalDateTime blockadeEnd;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "account_state", nullable = false)
  private AccountState accountState;

  @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
      CascadeType.MERGE}, orphanRemoval = true)
  @Builder.Default
  private List<AccessLevel> accessLevels = new ArrayList<>();

  @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
  @Builder.Default
  @ToString.Exclude
  private List<Order> orders = new ArrayList<>();

  @Column(name = "new_email")
  private String newEmail;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "type", nullable = false)
  private AccountType accountType;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "time_zone", nullable = false)
  private TimeZone timeZone;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @Builder.Default
  @ToString.Exclude
  @JoinColumn(name = "account_id")
  private List<PasswordHistory> passwordHistory = new ArrayList<>();

  @Column(name = "force_password_change", columnDefinition = "boolean default false not null")
  @Builder.Default
  private Boolean forcePasswordChange = false;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
  @JoinColumn(name = "account_search_settings_id", nullable = false)
  private AccountSearchSettings accountSearchSettings;

  public void update(Account account) {
    this.email = account.email != null ? account.email : email;
    this.person.update(account.getPerson());
  }

  public Long getSumOfVersions() {
    return this.getVersion() + this.getPerson().getVersion() + this.getPerson().getAddress().getVersion();
  }
}
