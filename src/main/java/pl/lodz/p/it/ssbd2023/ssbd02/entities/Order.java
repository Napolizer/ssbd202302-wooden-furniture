package pl.lodz.p.it.ssbd2023.ssbd02.entities;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ExcludeSuperclassListeners;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
import pl.lodz.p.it.ssbd2023.ssbd02.utils.listeners.OrderListener;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity(name = "sales_order")
@ExcludeSuperclassListeners
@EntityListeners(OrderListener.class)
@Table(name = "sales_order", indexes = {
    @Index(name = "sales_order_account_id", columnList = "account_id"),
    @Index(name = "sales_order_delivery_person_id", columnList = "recipient_id", unique = true),
    @Index(name = "sales_order_delivery_address_id", columnList = "delivery_address_id", unique = true)
})
@NamedQueries({
    @NamedQuery(
        name = Order.FIND_ACCOUNT_ORDERS,
        query = "SELECT o FROM sales_order o"
            + " WHERE o.account.login = :login "
    ),
    @NamedQuery(
        name = Order.FIND_BY_STATE,
        query = "SELECT o FROM sales_order o "
        + "WHERE o.orderState = :orderState"
    ),
    @NamedQuery(
        name = Order.FIND_ORDER_STATS_FOR_REPORT,
        query = "SELECT pg.name, pg.averageRating, "
                + "COALESCE(SUM(p.amount), 0), "
                + "COALESCE(SUM(CASE WHEN so.createdAt >= :startDate "
                + "AND so.createdAt <= :endDate AND so.orderState != :createdState "
                + "AND so.orderState != :cancelledState THEN sop.amount ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN so.createdAt >= :startDate "
                + "AND so.createdAt <= :endDate AND so.orderState != :createdState "
                + "AND so.orderState != :cancelledState THEN sop.price * sop.amount ELSE 0 END), 0)"
                + " as total_price "
                + "FROM product_group pg "
                + "LEFT JOIN pg.products p "
                + "LEFT JOIN sales_order_product sop ON sop.product = p "
                + "LEFT JOIN sop.order so "
                + "GROUP BY pg.name, pg.averageRating "
                + "ORDER BY total_price DESC"
    ),
    @NamedQuery(
                name = Order.FIND_WITH_FILTERS,
                query = "SELECT o FROM sales_order o "
                        + "JOIN o.orderedProducts sop "
                        + "WHERE o.orderState = :orderState "
                        + "AND o.totalPrice BETWEEN :minPrice AND :maxPrice "
                        + "GROUP BY o.id "
                        + "HAVING SUM(sop.amount) = :totalAmount"
        ),
    @NamedQuery(
                name = Order.FIND_WITH_FILTERS_WITH_COMPANY,
                query = "SELECT o FROM sales_order o "
                        + "JOIN o.orderedProducts sop "
                        + "JOIN access_level al ON al.account.id = o.account.id "
                        + "JOIN client c ON al.id = c.id "
                        + "WHERE o.orderState = :orderState "
                        + "AND o.totalPrice BETWEEN :minPrice AND :maxPrice "
                        + "AND c.company.id IS NOT NULL "
                        + "GROUP BY o.id "
                        + "HAVING SUM(sop.amount) = :totalAmount"
        )
})
public class Order extends AbstractEntity {
  public static final String FIND_ACCOUNT_ORDERS = "Order.findAccountOrders";
  public static final String FIND_BY_STATE = "Order.findByState";
  public static final String FIND_ORDER_STATS_FOR_REPORT = "Order.findOrderStatsForReport";
  public static final String FIND_WITH_FILTERS = "Order.findWithFilters";
  public static final String FIND_WITH_FILTERS_WITH_COMPANY = "Order.findWithFiltersWithCompany";

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false, name = "order_state")
  private OrderState orderState;

  @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private List<OrderedProduct> orderedProducts = new ArrayList<>();

  @Column(nullable = false, name = "total_price", updatable = false)
  private Double totalPrice;

  @Column(name = "recipient_first_name", nullable = false, updatable = false)
  private String recipientFirstName;

  @Column(name = "recipient_last_name", nullable = false, updatable = false)
  private String recipientLastName;

  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "delivery_address_id", nullable = false, updatable = false)
  private Address deliveryAddress;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false, updatable = false)
  private Account account;

  @Column(name = "observed", columnDefinition = "boolean default false not null")
  @Builder.Default
  private Boolean observed = false;

  @Column(name = "modification_block_time", nullable = false)
  private LocalDateTime modificationBlockTime;

  public Long getSumOfVersions() {
    Long sumOfProductsVersions = 0L;
    for (OrderedProduct product : this.getOrderedProducts()) {
      sumOfProductsVersions += product.getVersion();
    }
    return this.getVersion() + sumOfProductsVersions + this.getDeliveryAddress().getVersion()
        + this.getAccount().getSumOfVersions();
  }
}
