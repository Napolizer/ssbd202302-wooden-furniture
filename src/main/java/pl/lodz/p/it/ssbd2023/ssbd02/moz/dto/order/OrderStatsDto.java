package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatsDto {
  @NotNull
  private String productGroupName;
  @NotNull
  private Double averageRating;
  @NotNull
  private Integer amount;
  @NotNull
  private Integer soldAmount;
  @NotNull
  private Double totalIncome;
}
