package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product extends AbstractEntity {
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private Boolean available;
    private Byte[] image;
    @Column(nullable = false)
    private Double weight;
    private Integer amount;
    @Column(name = "weight_in_package")
    private Double weightInPackage;
    @Embedded
    @Column(name = "furniture_dimensions")
    private Dimensions furnitureDimensions;
    @Embedded
    @Column(name = "package_dimensions")
    private Dimensions packageDimensions;
    @Enumerated(value = EnumType.STRING)
    private Color color;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "wood_type")
    private WoodType woodType;
}
