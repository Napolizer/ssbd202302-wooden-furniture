package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rate", indexes = {
    @Index(name = "rate_person_id", columnList = "person_id", unique = true),
    @Index(name = "rate_product_group_id", columnList = "product_group_id", unique = true)
})
public class Rate extends AbstractEntity {

  @Column(nullable = false)
  @Min(1)
  @Max(5)
  private Integer value;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @ManyToOne
  @JoinColumn(name = "product_group_id", nullable = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private ProductGroup productGroup;
}
