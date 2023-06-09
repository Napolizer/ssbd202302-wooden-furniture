package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "sales_order_product", indexes = {
    @Index(name = "sales_order_product_product_id", columnList = "product_id"),
    @Index(name = "sales_order_product_order_id", columnList = "product_id"),
})
public class OrderProduct extends AbstractEntity {

  @Column(nullable = false, name = "amount", updatable = false)
  private Integer amount;

  @Column(nullable = false, name = "price", updatable = false)
  private Double price;

  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false, updatable = false)
  private Order order;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false, updatable = false)
  private Product product;

}
