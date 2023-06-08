package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.ProductState;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateDto {

  @Positive
  @NotNull
  private Double price;

  @NotNull
  private ProductState productState;

  @Positive
  @NotNull
  private Integer amount;

  @Positive
  @NotNull
  private Double weight;

  @Positive
  @NotNull
  private Double weightInPackage;

  @Positive
  @NotNull
  private Double furnitureWidth;

  @Positive
  @NotNull
  private Double furnitureHeight;

  @Positive
  @NotNull
  private Double furnitureDepth;

  @Positive
  @NotNull
  private Double packageWidth;

  @Positive
  @NotNull
  private Double packageHeight;

  @Positive
  @NotNull
  private Double packageDepth;

  @NotNull
  private String color;

  @NotNull
  private String woodType;

  @NotNull
  @Positive
  private Long productGroupId;

}
