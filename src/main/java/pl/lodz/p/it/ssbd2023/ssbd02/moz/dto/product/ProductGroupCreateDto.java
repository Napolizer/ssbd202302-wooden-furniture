package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Capitalized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductGroupCreateDto {

  @Capitalized
  private String name;

  @NotNull
  private String categoryName;
}
