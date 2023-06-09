package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RateCreateDto {
  @Min(1)
  @Max(5)
  @NotNull
  private Integer rate;
  @NotNull
  private Long productId;
}
