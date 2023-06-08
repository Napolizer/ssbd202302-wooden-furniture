package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity(name = "sales_order")
@Table(name = "sales_order", indexes = {
    @Index(name = "sales_order_account_id", columnList = "account_id", unique = true),
    @Index(name = "sales_order_delivery_person_id", columnList = "recipient_id", unique = true),
    @Index(name = "sales_order_delivery_address_id", columnList = "delivery_address_id", unique = true)
})
@NamedQueries({
    @NamedQuery(
        name = Order.FIND_CLIENTS_DELIVERED_ORDERS,
        query = "SELECT o FROM sales_order o"
            + " WHERE o.account.id = :id "
            + "AND (o.orderState = pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.COMPLETED"
            + " OR o.orderState = pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.DELIVERED)"
    ),
    @NamedQuery(
        name = Order.FIND_BY_STATE,
        query = "SELECT o FROM sales_order o "
        + "WHERE o.orderState = :orderState"
    )
})
public class Order extends AbstractEntity {
  public static final String FIND_CLIENTS_DELIVERED_ORDERS = "Order.findClientsDeliveredOrders";
  public static final String FIND_BY_STATE = "Order.findByState";

  @Column(nullable = false, updatable = false, name = "creation_date")
  private LocalDateTime creationDate;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false, name = "order_state")
  private OrderState orderState;

  @ManyToMany
  @JoinTable(
      name = "sales_order_product",
      joinColumns = @JoinColumn(name = "order_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id"),
      indexes = @Index(name = "sales_order_product_product_id", columnList = "product_id", unique = true)
  )
  private List<Product> products = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "recipient_id", nullable = false, updatable = false)
  private Person recipient;

  @OneToOne
  @JoinColumn(name = "delivery_address_id", nullable = false, updatable = false)
  private Address deliveryAddress;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false, updatable = false)
  private Account account;

  @Column(name = "observed", columnDefinition = "boolean default false not null")
  @Builder.Default
  private Boolean observed = false;

  @PrePersist
  public void init() {
    creationDate = LocalDateTime.now();
  }

  public Long getSumOfVersions() {
    Long sumOfProductsVersions = 0L;
    for (Product product : this.getProducts()) {
      sumOfProductsVersions += product.getVersion();
    }
    return this.getVersion() + sumOfProductsVersions + this.getRecipient().getVersion()
        + this.getDeliveryAddress().getVersion() + this.getAccount().getSumOfVersions();
  }
}
