package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@SuperBuilder
@Table(name = "account_search_settings")
@NamedQueries({
    @NamedQuery(name = "accountListPreferences.findByAccount",
                query = "SELECT p FROM AccountSearchSettings p WHERE p.account = :account")
})
public class AccountSearchSettings extends AbstractEntity {
  @NotNull
  @OneToOne
  @JoinColumn(name = "account_id")
  private Account account;
  @Column(name = "search_page", columnDefinition = "integer default 0")
  private Integer searchPage;
  @Column(name = "displayed_accounts", columnDefinition = "integer default 3")
  private Integer displayedAccounts;
  @Column(name = "search_keyword", columnDefinition = "varchar(3) default ''")
  private String searchKeyword;
  @Column(name = "sort_by")
  @Enumerated(value = EnumType.STRING)
  private SortBy sortBy;
  @Column(name = "sort_ascending", columnDefinition = "boolean default true")
  private Boolean sortAscending;
}
