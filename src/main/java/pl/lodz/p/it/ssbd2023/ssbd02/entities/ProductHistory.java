package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class ProductHistory extends AbstractEntity {
  @Enumerated(value = EnumType.STRING)
  @Column(name = "field_name", nullable = false, updatable = false)
  private ProductField fieldName;

  @Column(name = "old_value", nullable = false, updatable = false)
  private Double oldValue;

  @Column(name = "new_value", nullable = false, updatable = false)
  private Double newValue;

}
