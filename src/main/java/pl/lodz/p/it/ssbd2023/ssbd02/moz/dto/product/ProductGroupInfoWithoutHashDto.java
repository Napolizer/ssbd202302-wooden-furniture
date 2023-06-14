package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.category.CategoryDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductGroupInfoWithoutHashDto {
  @NotNull
  private Long id;

  @NotNull
  private String name;

  @NotNull
  private Double averageRating;

  @NotNull
  private CategoryDto category;

  @NotNull
  private Boolean archive;
}
