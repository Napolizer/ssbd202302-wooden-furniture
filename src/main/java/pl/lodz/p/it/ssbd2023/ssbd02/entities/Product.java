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

    @Lob
    private Byte[] image;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "weight_in_package", nullable = false)
    private Double weightInPackage;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "width", column = @Column(name = "furniture_width")),
            @AttributeOverride(name = "height", column = @Column(name = "furniture_height")),
            @AttributeOverride(name = "depth", column = @Column(name = "furniture_depth"))
    })
    @Column(nullable = false)
    private Dimensions furnitureDimensions;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "width", column = @Column(name = "package_width")),
            @AttributeOverride(name = "height", column = @Column(name = "package_height")),
            @AttributeOverride(name = "depth", column = @Column(name = "package_depth"))
    })
    @Column(nullable = false)
    private Dimensions packageDimensions;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Color color;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "wood_type", nullable = false)
    private WoodType woodType;
}
