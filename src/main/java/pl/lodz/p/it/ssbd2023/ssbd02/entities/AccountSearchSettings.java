package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@Entity
@SuperBuilder
@AllArgsConstructor
@Table(name = "account_search_settings")
public class AccountSearchSettings extends AbstractEntity {
  @Column(name = "search_page", columnDefinition = "integer default 0")
  private Integer searchPage;
  @Column(name = "displayed_accounts", columnDefinition = "integer default 3")
  private Integer displayedAccounts;
  @Column(name = "search_keyword", columnDefinition = "varchar(256) default ''")
  private String searchKeyword;
  @Column(name = "sort_by")
  @Enumerated(value = EnumType.STRING)
  private SortBy sortBy;
  @Column(name = "sort_ascending", columnDefinition = "boolean default true")
  private Boolean sortAscending;

  public AccountSearchSettings() {
    searchPage = 0;
    displayedAccounts = 10;
    searchKeyword = "";
    sortBy = SortBy.LOGIN;
    sortAscending = true;
  }
}
