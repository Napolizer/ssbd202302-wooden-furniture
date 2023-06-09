package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.ProductState;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {

  @Positive
  @NotNull
  private Double price;

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
  private Integer furnitureWidth;

  @Positive
  @NotNull
  private Integer furnitureHeight;

  @Positive
  @NotNull
  private Integer furnitureDepth;

  @Positive
  @NotNull
  private Integer packageWidth;

  @Positive
  @NotNull
  private Integer packageHeight;

  @Positive
  @NotNull
  private Integer packageDepth;

  @NotNull
  private String color;

  @NotNull
  private String woodType;

  @NotNull
  @Positive
  private Long productGroupId;

}
