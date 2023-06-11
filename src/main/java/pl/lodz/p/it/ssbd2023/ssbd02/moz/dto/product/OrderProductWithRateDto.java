package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductWithRateDto {
  @NotNull
  @Positive
  private Integer amount;
  @NotNull
  @Positive
  private Double price;
  private Integer rate;
  @NotNull
  @Valid
  private ProductDto product;
}
