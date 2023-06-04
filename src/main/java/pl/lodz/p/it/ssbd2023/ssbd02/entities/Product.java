package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
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
      @AttributeOverride(name = "width", column = @Column(name = "furniture_width", nullable = false)),
      @AttributeOverride(name = "height", column = @Column(name = "furniture_height", nullable = false)),
      @AttributeOverride(name = "depth", column = @Column(name = "furniture_depth", nullable = false))
  })
  @Column(nullable = false)
  private Dimensions furnitureDimensions;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "width", column = @Column(name = "package_width", nullable = false)),
      @AttributeOverride(name = "height", column = @Column(name = "package_height", nullable = false)),
      @AttributeOverride(name = "depth", column = @Column(name = "package_depth", nullable = false))
  })
  @Column(nullable = false)
  private Dimensions packageDimensions;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  private Color color;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "wood_type", nullable = false)
  private WoodType woodType;

  @ManyToOne
  @JoinColumn(name = "product_group_id", nullable = false)
  private ProductGroup productGroup;
}
