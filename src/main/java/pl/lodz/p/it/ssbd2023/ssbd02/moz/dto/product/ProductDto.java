package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Dimensions;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    @NotNull
    private Long id;
    @NotNull
    private Double price;
    private Boolean available;
    private String image;
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
}
