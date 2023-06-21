package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ExcludeSuperclassListeners;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.listeners.RateListener;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@ExcludeSuperclassListeners
@EntityListeners(RateListener.class)
@Table(name = "rate", indexes = {
    @Index(name = "rate_person_id", columnList = "person_id", unique = true),
    @Index(name = "rate_product_group_id", columnList = "product_group_id", unique = true)
})
public class Rate extends AbstractEntity {

  @Column(nullable = false)
  private Integer value;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @ManyToOne
  @JoinColumn(name = "product_group_id", nullable = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private ProductGroup productGroup;

  @Column(nullable = false)
  private LocalDateTime modificationBlockTime;

  public Rate(Integer value, Account account, ProductGroup productGroup) {
    this.value = value;
    this.account = account;
    this.productGroup = productGroup;
  }
}
