package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
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
}
