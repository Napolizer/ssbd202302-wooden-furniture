package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditProductDto {
  @NotNull
  private Double price;
  @NotNull
  private Integer amount;
  @NotNull
  private String hash;

  @Override
  public String toString() {
    return "EditPersonInfoDto{}";
  }
}
