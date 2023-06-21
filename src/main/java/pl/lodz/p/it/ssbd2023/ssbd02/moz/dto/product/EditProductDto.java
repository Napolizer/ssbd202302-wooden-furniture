package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Amount;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Hash;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Price;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditProductDto {
  @NotNull
  @Price
  private Double price;
  @Amount
  private Integer amount;
  @NotNull
  @Hash
  private String hash;

  @Override
  public String toString() {
    return "EditProductDto{}";
  }
}
