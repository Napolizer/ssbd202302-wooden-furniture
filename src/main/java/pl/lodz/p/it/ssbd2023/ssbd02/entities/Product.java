package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(name = "product_product_group_id", columnList = "product_group_id"),
                  @Index(name = "product_image_id", columnList = "image_id")},
       uniqueConstraints = { @UniqueConstraint(name = "product_details",
               columnNames = {"product_group_id",
                              "color",
                              "wood_type",
                              "weight",
                              "furniture_width",
                              "furniture_height",
                              "furniture_depth" }) })
@NamedQueries({
    @NamedQuery(name = Product.FIND_BY_PRODUCT_ID,
        query = "SELECT product FROM Product product WHERE product.id = :id"),
    @NamedQuery(name = Product.FIND_ALL_BY_PRODUCT_GROUP_COLOR_AND_WOOD_TYPE,
        query = "SELECT product FROM Product product WHERE product.productGroup.id = :productGroupId "
                + "AND (:color IS NULL OR product.color = :color) "
                + "AND (:woodType IS NULL OR product.woodType = :woodType)"),
    @NamedQuery(name = Product.FIND_ALL_BY_CATEGORY_ID,
        query = "SELECT product FROM Product product WHERE product.productGroup.category.id = :categoryId"),
    @NamedQuery(name = Product.FIND_ALL_BY_PRODUCT_GROUP_ID,
        query = "SELECT product FROM Product product WHERE product.productGroup.id = :productGroupId")
})
public class Product extends AbstractEntity {
  public static final String FIND_BY_PRODUCT_ID = "Product.findByProductId";
  public static final String
          FIND_ALL_BY_PRODUCT_GROUP_COLOR_AND_WOOD_TYPE = "Product.findAllByProductGroupColorAndWoodType";
  public static final String FIND_ALL_BY_PRODUCT_GROUP_ID = "Product.findAllByProductGroupId";
  public static final String FIND_ALL_BY_CATEGORY_ID = "Product.findAllByCategoryId";

  @Column(nullable = false)
  private Double price;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "image_id", nullable = false)
  private Image image;

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

  @Builder.Default
  private Boolean isUpdatedBySystem = false;

  @OneToMany(fetch = FetchType.LAZY)
  @Builder.Default
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = "product_id")
  private List<ProductHistory> productHistory = new ArrayList<>();

  public Long getSumOfVersions() {
    return this.getVersion();
  }

  public void update(Product product) {
    this.price = product.price != null ? product.price : price;
    this.amount = product.amount != null ? product.amount : amount;
  }
}
