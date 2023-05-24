package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.SortBy;

@Data
@AllArgsConstructor
public class AccountSearchSettingsDto {
  @Positive
  private Integer searchPage;
  @Positive
  private Integer displayedAccounts;
  private String searchKeyword;
  private SortBy sortBy;
  private Boolean sortAscending;

  public AccountSearchSettingsDto() {
    searchPage = 1;
    displayedAccounts = 10;
    searchKeyword = "";
    sortBy = SortBy.LOGIN;
    sortAscending = true;
  }
}
