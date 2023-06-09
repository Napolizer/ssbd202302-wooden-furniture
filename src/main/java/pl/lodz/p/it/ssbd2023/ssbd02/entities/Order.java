package pl.lodz.p.it.ssbd2023.ssbd02.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    @Index(name = "sales_order_account_id", columnList = "account_id"),
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

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false, name = "order_state")
  private OrderState orderState;

  @OneToMany(mappedBy = "order")
  private List<OrderProduct> orderedProducts = new ArrayList<>();

  @Column(nullable = false, name = "total_price", updatable = false)
  private Double totalPrice;

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

  public Long getSumOfVersions() {
    Long sumOfProductsVersions = 0L;
    for (OrderProduct product : this.getOrderedProducts()) {
      sumOfProductsVersions += product.getVersion();
    }
    return this.getVersion() + sumOfProductsVersions + this.getRecipient().getVersion()
        + this.getDeliveryAddress().getVersion() + this.getAccount().getSumOfVersions();
  }
}
