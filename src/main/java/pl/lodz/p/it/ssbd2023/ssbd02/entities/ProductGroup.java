package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "product_group")
@Table(name = "product_group",
    indexes = @Index(name = "product_group_category_id", columnList = "category_id"))
@NamedQueries({
    @NamedQuery(name = ProductGroup.FIND_BY_ID,
            query = "SELECT productGroup FROM product_group productGroup WHERE productGroup.id = :id"),
    @NamedQuery(name = ProductGroup.GET_AVG_RATE,
            query = "SELECT AVG(rate.value) FROM product_group productGroup JOIN productGroup.rates rate"
            + " WHERE productGroup.id = :productGroupId")
})
public class ProductGroup extends AbstractEntity {

  public static final String FIND_BY_ID = "ProductGroup.findById";
  public static final String GET_AVG_RATE = "ProductGroup.getAverageRate";

  @Column(nullable = false, unique = true)
  private String name;

  @Column(name = "average_rating")
  private Double averageRating;

  @OneToMany
  @JoinColumn(name = "product_group_id", nullable = false)
  @Builder.Default
  private List<Product> products = new ArrayList<>();

  @OneToMany(mappedBy = "productGroup")
  @JoinColumn(name = "product_group_id", nullable = false)
  @Builder.Default
  private List<Rate> rates = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

}
