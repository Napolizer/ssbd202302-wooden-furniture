package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.CapitalizedLong;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.ProductGroupName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditProductGroupDto {
  @ProductGroupName
  @NotNull
  private String name;

  @NotNull
  private String hash;
}
