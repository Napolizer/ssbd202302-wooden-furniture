package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.SortBy;

@Data
@Builder
@AllArgsConstructor
public class AccountSearchSettingsDto {
  @Positive
  @NotNull
  private Integer searchPage;
  @Positive
  @NotNull
  private Integer displayedAccounts;
  @NotNull
  private String searchKeyword;
  @NotNull
  private SortBy sortBy;
  @NotNull
  private Boolean sortAscending;

  public AccountSearchSettingsDto() {
    searchPage = 0;
    displayedAccounts = 10;
    searchKeyword = "";
    sortBy = SortBy.LOGIN;
    sortAscending = true;
  }
}
