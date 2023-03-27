package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedQueries({
        @NamedQuery(name = Product.FIND_ALL_BY_WOOD_TYPE,
        query = "SELECT product FROM Product product WHERE product.woodType = :woodType"),
        @NamedQuery(name = Product.FIND_ALL_BY_COLOR,
        query = "SELECT product FROM Product product WHERE product.color = :color"),
        @NamedQuery(name = Product.FIND_ALL_AVAILABLE,
        query = "SELECT product FROM Product product WHERE product.available = true"),
        @NamedQuery(name = Product.FIND_ALL_BY_PRICE,
        query = "SELECT product FROM Product product WHERE product.price BETWEEN :minPrice AND :maxPrice")
})
public class Product extends AbstractEntity {
    public static final String FIND_ALL_BY_WOOD_TYPE = "Product.findAllByWoodType";
    public static final String FIND_ALL_BY_COLOR = "Product.findAllByColor";
    public static final String FIND_ALL_AVAILABLE = "Product.findAllAvailable";
    public static final String FIND_ALL_BY_PRICE = "Product.findAllByPrice";

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
