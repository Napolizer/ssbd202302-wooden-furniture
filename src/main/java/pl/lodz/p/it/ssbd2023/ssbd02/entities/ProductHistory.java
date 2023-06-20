package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.ProductField;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "product_history")
@NamedQueries({
    @NamedQuery(name = ProductHistory.FIND_ALL_DISCOUNTS_BY_EMPLOYEE_OF_PRODUCT_IN_CURRENT_MONTH,
            query = "SELECT ph FROM ProductHistory ph "
                    + "JOIN Product p "
                    + "WHERE ph.id = :productId AND ph.createdBy.id = :accountId AND ph.newValue < ph.oldValue "
                    + "AND ph.fieldName = :price AND p.price = ph.newValue "
                    + "AND extract(MONTH from :now) - extract(MONTH from ph.createdAt) = 0"),
})
public class ProductHistory extends AbstractEntity {

  public static final String FIND_ALL_DISCOUNTS_BY_EMPLOYEE_OF_PRODUCT_IN_CURRENT_MONTH
          = "ProductHistory.findAllDiscountsByEmployeeOfProductInCurrentMonth";

  @Enumerated(value = EnumType.STRING)
  @Column(name = "field_name", nullable = false, updatable = false)
  private ProductField fieldName;

  @Column(name = "old_value", nullable = false, updatable = false)
  private Double oldValue;

  @Column(name = "new_value", nullable = false, updatable = false)
  private Double newValue;

}
