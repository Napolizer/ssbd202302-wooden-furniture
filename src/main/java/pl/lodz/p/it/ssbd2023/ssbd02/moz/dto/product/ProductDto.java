package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Dimensions;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
  @NotNull
  private Long id;
  @NotNull
  private Double price;
  private Boolean archive;
  @NotNull
  private String imageUrl;
  @NotNull
  private Double weight;
  @NotNull
  private Integer amount;
  @NotNull
  private Double weightInPackage;
  @NotNull
  private Dimensions furnitureDimensions;
  @NotNull
  private Dimensions packageDimensions;
  @NotNull
  private Color color;
  @NotNull
  private WoodType woodType;
  @NotNull
  private ProductGroupInfoWithoutHashDto productGroup;
  @NotNull
  private String hash;
}

